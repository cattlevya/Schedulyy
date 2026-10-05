package com.pemmob.schedulyy.ui.scan

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.schedulyy.data.local.dao.CourseWithSessions
import com.pemmob.schedulyy.data.local.entity.ClassSessionEntity
import com.pemmob.schedulyy.data.local.entity.CourseEntity
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import com.pemmob.schedulyy.data.local.entity.TaskEntity
import com.pemmob.schedulyy.data.remote.gemini.GeminiScheduleExtractor
import com.pemmob.schedulyy.data.util.ImagePreprocessor
import com.pemmob.schedulyy.data.util.ProcessedImage
import com.pemmob.schedulyy.domain.model.ScanReviewData
import com.pemmob.schedulyy.domain.model.ScanScheduleCandidate
import com.pemmob.schedulyy.domain.model.ScanTaskCandidate
import com.pemmob.schedulyy.domain.model.TaskPriority
import com.pemmob.schedulyy.domain.model.TaskSource
import com.pemmob.schedulyy.domain.model.TaskStatus
import com.pemmob.schedulyy.domain.repository.ScheduleRepository
import com.pemmob.schedulyy.domain.repository.SettingsRepository
import com.pemmob.schedulyy.domain.repository.SpaceRepository
import com.pemmob.schedulyy.domain.repository.TaskRepository
import com.pemmob.schedulyy.domain.util.DuplicateDetectorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ScanUiState(
    val selectedUris: List<Uri> = emptyList(),
    val processedImages: List<ProcessedImage> = emptyList(),
    val activeSpaces: List<SpaceEntity> = emptyList(),
    val selectedSpace: SpaceEntity? = null,
    val hasApiKey: Boolean = false,
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val reviewData: ScanReviewData? = null,
    val errorMessage: String? = null,
    val saveSummaryMessage: String? = null,
    val canUndo: Boolean = false
)

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val imagePreprocessor: ImagePreprocessor,
    private val geminiExtractor: GeminiScheduleExtractor,
    private val settingsRepository: SettingsRepository,
    private val spaceRepository: SpaceRepository,
    private val scheduleRepository: ScheduleRepository,
    private val taskRepository: TaskRepository,
    private val reminderScheduler: com.pemmob.schedulyy.data.notification.ReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private var extractionJob: Job? = null
    private val lastInsertedTaskIds = mutableListOf<Long>()
    private val lastInsertedCourseIds = mutableListOf<Long>()

    init {
        viewModelScope.launch {
            settingsRepository.getUserSettings().collect { settings ->
                _uiState.value = _uiState.value.copy(hasApiKey = settings.hasApiKey)
            }
        }

        viewModelScope.launch {
            spaceRepository.getAllActiveSpaces().collect { spaces ->
                val current = _uiState.value.selectedSpace
                val defaultSpace = current ?: spaces.firstOrNull { it.id == 2L } ?: spaces.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    activeSpaces = spaces,
                    selectedSpace = defaultSpace
                )
            }
        }
    }

    fun onImagesSelected(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val currentUris = (_uiState.value.selectedUris + uris).distinct().take(5)
        _uiState.value = _uiState.value.copy(
            selectedUris = currentUris,
            errorMessage = null
        )

        // Proses gambar di background
        viewModelScope.launch {
            val processedList = mutableListOf<ProcessedImage>()
            for (uri in currentUris) {
                val processed = imagePreprocessor.processImageUri(uri)
                if (processed != null) {
                    processedList.add(processed)
                }
            }
            _uiState.value = _uiState.value.copy(processedImages = processedList)
        }
    }

    fun onRemoveImage(index: Int) {
        val currentUris = _uiState.value.selectedUris.toMutableList()
        val currentProcessed = _uiState.value.processedImages.toMutableList()
        if (index in currentUris.indices) {
            currentUris.removeAt(index)
        }
        if (index in currentProcessed.indices) {
            currentProcessed.removeAt(index)
        }
        _uiState.value = _uiState.value.copy(
            selectedUris = currentUris,
            processedImages = currentProcessed
        )
    }

    fun onSelectSpace(space: SpaceEntity) {
        _uiState.value = _uiState.value.copy(selectedSpace = space)
    }

    fun startExtraction() {
        val images = _uiState.value.processedImages
        if (images.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Pilih minimal 1 gambar screenshot untuk dipindai.")
            return
        }

        val space = _uiState.value.selectedSpace
        val spaceName = space?.name ?: "Kuliah"

        extractionJob?.cancel()
        extractionJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                loadingMessage = "Membaca screenshot dengan Gemini AI…",
                errorMessage = null
            )

            val apiKey = settingsRepository.getDecryptedApiKey()
            if (apiKey.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "API key belum diisi. Buka Pengaturan untuk memasukkan key."
                )
                return@launch
            }

            val settings = settingsRepository.getUserSettings().first()
            val model = settings.selectedModel

            // Ambil daftar matkul yang sudah ada untuk konteks
            val existingCourses = scheduleRepository.getAllCoursesWithSessions().first()
            val existingCourseNames = existingCourses.map { it.course.name }.distinct()
            val existingTasks = taskRepository.getAllTasksWithSubTasks().first().map { it.task }

            val result = geminiExtractor.extractFromImages(
                apiKey = apiKey,
                model = model,
                images = images,
                spaceName = spaceName,
                existingCourses = existingCourseNames
            )

            if (result.isSuccess) {
                val rawReviewData = result.getOrNull() ?: ScanReviewData()

                // Cek duplikasi dengan data yang sudah ada di Room (§6.6)
                val checkedSchedules = DuplicateDetectorUtil.checkScheduleDuplicates(
                    rawReviewData.schedules,
                    existingCourses
                )
                val checkedTasks = DuplicateDetectorUtil.checkTaskDuplicates(
                    rawReviewData.tasks,
                    existingTasks
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reviewData = ScanReviewData(
                        schedules = checkedSchedules,
                        tasks = checkedTasks,
                        warnings = rawReviewData.warnings
                    )
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Terjadi kesalahan saat mengekstrak gambar."
                )
            }
        }
    }

    fun cancelExtraction() {
        extractionJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            loadingMessage = ""
        )
    }

    fun toggleScheduleSelection(id: String) {
        val review = _uiState.value.reviewData ?: return
        val updated = review.schedules.map {
            if (it.id == id) it.copy(isSelected = !it.isSelected) else it
        }
        _uiState.value = _uiState.value.copy(reviewData = review.copy(schedules = updated))
    }

    fun updateScheduleCandidate(candidate: ScanScheduleCandidate) {
        val review = _uiState.value.reviewData ?: return
        val updated = review.schedules.map {
            if (it.id == candidate.id) candidate else it
        }
        _uiState.value = _uiState.value.copy(reviewData = review.copy(schedules = updated))
    }

    fun toggleTaskSelection(id: String) {
        val review = _uiState.value.reviewData ?: return
        val updated = review.tasks.map {
            if (it.id == id) it.copy(isSelected = !it.isSelected) else it
        }
        _uiState.value = _uiState.value.copy(reviewData = review.copy(tasks = updated))
    }

    fun updateTaskCandidate(candidate: ScanTaskCandidate) {
        val review = _uiState.value.reviewData ?: return
        val updated = review.tasks.map {
            if (it.id == candidate.id) candidate else it
        }
        _uiState.value = _uiState.value.copy(reviewData = review.copy(tasks = updated))
    }

    fun saveSelectedCandidates(onComplete: () -> Unit) {
        val review = _uiState.value.reviewData ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, loadingMessage = "Menyimpan data terpilih…")
            lastInsertedTaskIds.clear()
            lastInsertedCourseIds.clear()

            try {
                // Pastikan targetSpaceId adalah Space yang benar-benar ada di database
                val activeSpaces = spaceRepository.getAllActiveSpaces().first()
                val targetSpace = _uiState.value.selectedSpace
                    ?: activeSpaces.firstOrNull { it.type == com.pemmob.schedulyy.domain.model.SpaceType.KULIAH }
                    ?: activeSpaces.firstOrNull { it.id == 2L }
                    ?: activeSpaces.firstOrNull()
                    ?: run {
                        val defaultKuliah = SpaceEntity(
                            type = com.pemmob.schedulyy.domain.model.SpaceType.KULIAH,
                            name = "Kuliah",
                            colorKey = "pastelSky",
                            iconKey = "school"
                        )
                        val newSpaceId = spaceRepository.insertSpace(defaultKuliah)
                        spaceRepository.getSpaceById(newSpaceId)
                    }

                val targetSpaceId = targetSpace?.id ?: 1L
                val activeSemester = scheduleRepository.getActiveSemester().first()
                // PENTING: Jika tidak ada activeSemester, semesterId WAJIB NULL agar tidak melanggar foreign key constraint Room
                val targetSemesterId: Long? = activeSemester?.id

                var savedSchedulesCount = 0
                var savedTasksCount = 0

                // 1. Simpan Jadwal Kelas Terpilih
                val selectedSchedules = review.schedules.filter { it.isSelected }
                val existingCourses = scheduleRepository.getAllCoursesWithSessions().first()

                for (schedule in selectedSchedules) {
                    val normName = schedule.courseName.trim()
                    val existingCourseWithSessions = existingCourses.firstOrNull {
                        it.course.spaceId == targetSpaceId &&
                        it.course.name.equals(normName, ignoreCase = true)
                    }

                    // Parse jam mulai & selesai secara aman
                    val start = parseTimeSafe(schedule.startTime, LocalTime.of(8, 0))
                    val end = parseTimeSafe(schedule.endTime, start.plusMinutes(100))

                    if (existingCourseWithSessions != null) {
                        // Tambahkan sesi baru ke course yang sudah ada
                        val session = ClassSessionEntity(
                            courseId = existingCourseWithSessions.course.id,
                            dayOfWeek = schedule.dayOfWeek.coerceIn(1, 7),
                            startTime = start,
                            endTime = end,
                            room = schedule.room
                        )
                        val updatedSessions = existingCourseWithSessions.sessions + session
                        scheduleRepository.updateCourse(existingCourseWithSessions.course, updatedSessions)
                    } else {
                        // Buat course baru sekaligus dengan sesinya
                        val session = ClassSessionEntity(
                            courseId = 0L,
                            dayOfWeek = schedule.dayOfWeek.coerceIn(1, 7),
                            startTime = start,
                            endTime = end,
                            room = schedule.room
                        )
                        val newCourse = CourseEntity(
                            spaceId = targetSpaceId,
                            semesterId = targetSemesterId,
                            name = normName,
                            code = schedule.courseCode,
                            lecturer = schedule.lecturer,
                            room = schedule.room,
                            colorKey = "pastelButter",
                            iconKey = "book"
                        )
                        val newId = scheduleRepository.insertCourse(newCourse, listOf(session))
                        lastInsertedCourseIds.add(newId)
                    }
                    savedSchedulesCount++
                }

                // 2. Simpan Tugas Terpilih
                val selectedTasks = review.tasks.filter { it.isSelected }
                val zone = ZoneId.systemDefault()

                for (task in selectedTasks) {
                    var deadlineInstant: java.time.Instant? = null
                    var hasTime = false

                    if (!task.deadlineDate.isNullOrBlank()) {
                        try {
                            val cleanDateStr = task.deadlineDate.trim().replace('/', '-')
                            val date = LocalDate.parse(cleanDateStr)
                            if (!task.deadlineTime.isNullOrBlank()) {
                                val time = parseTimeSafe(task.deadlineTime, LocalTime.of(23, 59))
                                deadlineInstant = LocalDateTime.of(date, time).atZone(zone).toInstant()
                                hasTime = true
                            } else {
                                val time = LocalTime.of(23, 59)
                                deadlineInstant = LocalDateTime.of(date, time).atZone(zone).toInstant()
                                hasTime = false
                            }
                        } catch (e: Exception) {
                            // Abaikan parse error, simpan tanpa deadline
                        }
                    }

                    val taskEntity = TaskEntity(
                        spaceId = targetSpaceId,
                        title = task.title,
                        notes = task.description ?: task.sourceSnippet,
                        deadline = deadlineInstant,
                        hasTime = hasTime,
                        priority = task.priority,
                        status = TaskStatus.TODO,
                        source = TaskSource.AI_SCAN
                    )

                    val taskId = taskRepository.insertTask(taskEntity)
                    lastInsertedTaskIds.add(taskId)
                    savedTasksCount++
                }

                val summary = "$savedSchedulesCount jadwal dan $savedTasksCount tugas berhasil ditambahkan"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reviewData = null,
                    selectedUris = emptyList(),
                    processedImages = emptyList(),
                    saveSummaryMessage = summary,
                    canUndo = true
                )
                // Fase 7: Jadwalkan pengingat otomatis untuk data baru
                try {
                    reminderScheduler.scheduleAll()
                } catch (e: Exception) {
                    // Ignore background scheduler exception
                }
                onComplete()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal menyimpan: ${e.localizedMessage ?: "Terjadi kesalahan database"}"
                )
            }
        }
    }

    private fun parseTimeSafe(raw: String?, default: LocalTime): LocalTime {
        if (raw.isNullOrBlank()) return default
        return try {
            val clean = raw.trim().replace('.', ':')
            val parts = clean.split(':')
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: default.hour
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: default.minute
            LocalTime.of(hour.coerceIn(0, 23), minute.coerceIn(0, 59))
        } catch (e: Exception) {
            default
        }
    }

    fun undoSave() {
        viewModelScope.launch {
            for (taskId in lastInsertedTaskIds) {
                val task = taskRepository.getTaskById(taskId)
                if (task != null) {
                    taskRepository.deleteTask(task.task)
                }
            }
            for (courseId in lastInsertedCourseIds) {
                scheduleRepository.deleteCourseById(courseId)
            }
            lastInsertedTaskIds.clear()
            lastInsertedCourseIds.clear()
            _uiState.value = _uiState.value.copy(
                canUndo = false,
                saveSummaryMessage = "Penyimpanan hasil scan berhasil dibatalkan."
            )
            try {
                reminderScheduler.scheduleAll()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun clearSummaryMessage() {
        _uiState.value = _uiState.value.copy(saveSummaryMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
