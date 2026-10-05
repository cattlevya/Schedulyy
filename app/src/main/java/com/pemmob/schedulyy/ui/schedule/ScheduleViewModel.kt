package com.pemmob.schedulyy.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.schedulyy.data.local.dao.CourseWithSessions
import com.pemmob.schedulyy.data.local.entity.ClassSessionEntity
import com.pemmob.schedulyy.data.local.entity.CourseEntity
import com.pemmob.schedulyy.data.local.entity.SemesterEntity
import com.pemmob.schedulyy.domain.model.ClassSessionUiModel
import com.pemmob.schedulyy.domain.model.ScheduleViewMode
import com.pemmob.schedulyy.domain.model.SpaceType
import com.pemmob.schedulyy.domain.repository.ScheduleRepository
import com.pemmob.schedulyy.domain.repository.SpaceRepository
import com.pemmob.schedulyy.domain.util.ScheduleConflictUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.pemmob.schedulyy.data.local.entity.EventEntity
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import com.pemmob.schedulyy.data.notification.ReminderScheduler

data class EditableSession(
    val id: Long = 0,
    val dayOfWeek: Int = 1,
    val startTime: LocalTime = LocalTime.of(8, 0),
    val endTime: LocalTime = LocalTime.of(9, 40),
    val room: String = ""
)

data class AddEditCourseState(
    val isVisible: Boolean = false,
    val courseId: Long? = null,
    val name: String = "",
    val code: String = "",
    val lecturer: String = "",
    val defaultRoom: String = "",
    val sks: Int = 3,
    val colorKey: String = "sky",
    val sessions: List<EditableSession> = listOf(EditableSession()),
    val errorMessage: String? = null
)

data class AddEditSemesterState(
    val isVisible: Boolean = false,
    val semesterId: Long? = null,
    val name: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusMonths(5),
    val isActive: Boolean = true
)

data class ScheduleUiState(
    val viewMode: ScheduleViewMode = ScheduleViewMode.DAY,
    val selectedDate: LocalDate = LocalDate.now(),
    val activeSemester: SemesterEntity? = null,
    val coursesWithSessions: List<CourseWithSessions> = emptyList(),
    val daySessions: List<ClassSessionUiModel> = emptyList(),
    val weekSessions: Map<Int, List<ClassSessionUiModel>> = emptyMap(),
    val nextSessionToday: ClassSessionUiModel? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val spaceRepository: SpaceRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState(isLoading = true))
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val _courseSheetState = MutableStateFlow(AddEditCourseState())
    val courseSheetState: StateFlow<AddEditCourseState> = _courseSheetState.asStateFlow()

    private val _semesterSheetState = MutableStateFlow(AddEditSemesterState())
    val semesterSheetState: StateFlow<AddEditSemesterState> = _semesterSheetState.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _viewMode = MutableStateFlow(ScheduleViewMode.DAY)

    private data class ScheduleRawData(
        val courses: List<CourseWithSessions>,
        val semester: SemesterEntity?,
        val events: List<EventEntity>,
        val spaces: List<SpaceEntity>
    )

    init {
        viewModelScope.launch {
            val scheduleDataFlow = combine(
                scheduleRepository.getAllCoursesWithSessions(),
                scheduleRepository.getActiveSemester(),
                spaceRepository.getAllEvents(),
                spaceRepository.getAllActiveSpaces()
            ) { courses, semester, events, spaces ->
                ScheduleRawData(courses, semester, events, spaces)
            }

            combine(
                scheduleDataFlow,
                _selectedDate,
                _viewMode
            ) { rawData, selectedDate, viewMode ->
                val courses = rawData.courses
                val semester = rawData.semester
                val events = rawData.events
                val spaces = rawData.spaces
                val allCourseSessions = mapCoursesToUiSessions(courses)
                val allEventSessions = mapEventsToUiSessions(events, spaces, selectedDate)
                val allMappedSessions = ScheduleConflictUtil.calculateConflicts(allCourseSessions + allEventSessions)

                val dayOfWeek = selectedDate.dayOfWeek.value
                val daySessions = allMappedSessions.filter { it.dayOfWeek == dayOfWeek }

                val weekSessions = (1..7).associateWith { dow ->
                    allMappedSessions.filter { it.dayOfWeek == dow }
                }

                // Sesi berikutnya hari ini (jika ada)
                val todayDow = LocalDate.now().dayOfWeek.value
                val nowTime = LocalTime.now()
                val todayList = allMappedSessions.filter { it.dayOfWeek == todayDow }
                val nextToday = todayList
                    .filter { it.endTime.isAfter(nowTime) }
                    .minByOrNull { it.startTime }

                ScheduleUiState(
                    viewMode = viewMode,
                    selectedDate = selectedDate,
                    activeSemester = semester,
                    coursesWithSessions = courses,
                    daySessions = daySessions,
                    weekSessions = weekSessions,
                    nextSessionToday = nextToday,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun mapCoursesToUiSessions(courses: List<CourseWithSessions>): List<ClassSessionUiModel> {
        val rawList = mutableListOf<ClassSessionUiModel>()
        for (c in courses) {
            for (s in c.sessions) {
                rawList.add(
                    ClassSessionUiModel(
                        sessionId = s.id,
                        courseId = c.course.id,
                        courseName = c.course.name,
                        courseCode = c.course.code,
                        lecturer = c.course.lecturer,
                        room = s.room?.takeIf { it.isNotBlank() } ?: c.course.room,
                        sks = c.course.sks,
                        colorKey = c.course.colorKey,
                        iconKey = c.course.iconKey,
                        dayOfWeek = s.dayOfWeek,
                        startTime = s.startTime,
                        endTime = s.endTime
                    )
                )
            }
        }
        return rawList
    }

    private fun mapEventsToUiSessions(
        events: List<EventEntity>,
        spaces: List<SpaceEntity>,
        selectedDate: LocalDate
    ): List<ClassSessionUiModel> {
        val zone = ZoneId.systemDefault()
        val mondayOfSelectedWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sundayOfSelectedWeek = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        return events.mapNotNull { event ->
            val space = spaces.firstOrNull { it.id == event.spaceId }
            val startZdt = event.start.atZone(zone)
            val endZdt = event.end?.atZone(zone) ?: startZdt.plusHours(1)
            val eventDate = startZdt.toLocalDate()

            val isInSelectedWeek = event.repeatWeekly || (!eventDate.isBefore(mondayOfSelectedWeek) && !eventDate.isAfter(sundayOfSelectedWeek))
            if (!isInSelectedWeek) return@mapNotNull null

            ClassSessionUiModel(
                sessionId = -event.id,
                courseId = event.spaceId,
                courseName = event.title,
                courseCode = event.type.name,
                lecturer = space?.name,
                room = event.location,
                sks = null,
                colorKey = space?.colorKey ?: "mint",
                iconKey = "event",
                dayOfWeek = startZdt.dayOfWeek.value,
                startTime = startZdt.toLocalTime(),
                endTime = endZdt.toLocalTime()
            )
        }
    }

    fun setViewMode(mode: ScheduleViewMode) {
        _viewMode.value = mode
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    // ─── Course Form Actions ──────────────────────────────────────────────────

    fun openAddCourse() {
        val defaultDay = _selectedDate.value.dayOfWeek.value
        _courseSheetState.value = AddEditCourseState(
            isVisible = true,
            sessions = listOf(
                EditableSession(
                    dayOfWeek = defaultDay,
                    startTime = LocalTime.of(8, 0),
                    endTime = LocalTime.of(9, 40)
                )
            )
        )
    }

    fun openEditCourse(courseId: Long) {
        viewModelScope.launch {
            val c = scheduleRepository.getCourseWithSessions(courseId) ?: return@launch
            val editableSessions = c.sessions.map { s ->
                EditableSession(
                    id = s.id,
                    dayOfWeek = s.dayOfWeek,
                    startTime = s.startTime,
                    endTime = s.endTime,
                    room = s.room.orEmpty()
                )
            }
            _courseSheetState.value = AddEditCourseState(
                isVisible = true,
                courseId = c.course.id,
                name = c.course.name,
                code = c.course.code.orEmpty(),
                lecturer = c.course.lecturer.orEmpty(),
                defaultRoom = c.course.room.orEmpty(),
                sks = c.course.sks ?: 3,
                colorKey = c.course.colorKey,
                sessions = if (editableSessions.isNotEmpty()) editableSessions else listOf(EditableSession())
            )
        }
    }

    fun dismissCourseSheet() {
        _courseSheetState.update { it.copy(isVisible = false) }
    }

    fun updateCourseName(name: String) = _courseSheetState.update { it.copy(name = name, errorMessage = null) }
    fun updateCourseCode(code: String) = _courseSheetState.update { it.copy(code = code) }
    fun updateCourseLecturer(lecturer: String) = _courseSheetState.update { it.copy(lecturer = lecturer) }
    fun updateCourseRoom(room: String) = _courseSheetState.update { it.copy(defaultRoom = room) }
    fun updateCourseSks(sks: Int) = _courseSheetState.update { it.copy(sks = sks) }
    fun updateCourseColor(colorKey: String) = _courseSheetState.update { it.copy(colorKey = colorKey) }

    fun addSessionRow() {
        _courseSheetState.update { state ->
            val lastSession = state.sessions.lastOrNull()
            val nextDay = lastSession?.dayOfWeek ?: 1
            state.copy(
                sessions = state.sessions + EditableSession(
                    dayOfWeek = nextDay,
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(11, 40)
                )
            )
        }
    }

    fun removeSessionRow(index: Int) {
        _courseSheetState.update { state ->
            if (state.sessions.size > 1) {
                state.copy(sessions = state.sessions.filterIndexed { i, _ -> i != index })
            } else state
        }
    }

    fun updateSessionRow(
        index: Int,
        dayOfWeek: Int = -1,
        startTime: LocalTime? = null,
        endTime: LocalTime? = null,
        room: String? = null
    ) {
        _courseSheetState.update { state ->
            val updated = state.sessions.mapIndexed { i, session ->
                if (i == index) {
                    session.copy(
                        dayOfWeek = if (dayOfWeek in 1..7) dayOfWeek else session.dayOfWeek,
                        startTime = startTime ?: session.startTime,
                        endTime = endTime ?: session.endTime,
                        room = room ?: session.room
                    )
                } else session
            }
            state.copy(sessions = updated, errorMessage = null)
        }
    }

    fun saveCourse() {
        val state = _courseSheetState.value
        val nameTrimmed = state.name.trim()
        if (nameTrimmed.isEmpty()) {
            _courseSheetState.update { it.copy(errorMessage = "Nama matkul wajib diisi") }
            return
        }

        for (s in state.sessions) {
            if (!s.endTime.isAfter(s.startTime)) {
                _courseSheetState.update { it.copy(errorMessage = "Jam selesai harus setelah jam mulai") }
                return
            }
        }

        viewModelScope.launch {
            // Ambil Space jenis Kuliah atau fallback ke space pertama
            val activeSpaces = spaceRepository.getAllActiveSpaces().firstOrNull().orEmpty()
            val kuliahSpace = activeSpaces.firstOrNull { it.type == SpaceType.KULIAH }
                ?: activeSpaces.firstOrNull()

            val spaceId = kuliahSpace?.id ?: 2L
            val semesterId = _uiState.value.activeSemester?.id

            val courseEntity = CourseEntity(
                id = state.courseId ?: 0L,
                spaceId = spaceId,
                semesterId = semesterId,
                name = nameTrimmed,
                code = state.code.trim().takeIf { it.isNotEmpty() },
                lecturer = state.lecturer.trim().takeIf { it.isNotEmpty() },
                room = state.defaultRoom.trim().takeIf { it.isNotEmpty() },
                sks = state.sks,
                colorKey = state.colorKey,
                iconKey = "school"
            )

            val sessionEntities = state.sessions.map { s ->
                ClassSessionEntity(
                    id = s.id,
                    courseId = courseEntity.id,
                    dayOfWeek = s.dayOfWeek,
                    startTime = s.startTime,
                    endTime = s.endTime,
                    room = s.room.trim().takeIf { it.isNotEmpty() } ?: courseEntity.room
                )
            }

            if (state.courseId == null || state.courseId == 0L) {
                scheduleRepository.insertCourse(courseEntity, sessionEntities)
            } else {
                scheduleRepository.updateCourse(courseEntity, sessionEntities)
            }

            reminderScheduler.scheduleAll()
            _courseSheetState.value = AddEditCourseState(isVisible = false)
        }
    }

    fun deleteCourse(courseId: Long) {
        viewModelScope.launch {
            scheduleRepository.deleteCourseById(courseId)
            reminderScheduler.scheduleAll()
            _courseSheetState.value = AddEditCourseState(isVisible = false)
        }
    }

    // ─── Semester Sheet Actions ───────────────────────────────────────────────

    fun openSemesterSheet() {
        val current = _uiState.value.activeSemester
        _semesterSheetState.value = AddEditSemesterState(
            isVisible = true,
            semesterId = current?.id,
            name = current?.name.orEmpty(),
            startDate = current?.startDate ?: LocalDate.now(),
            endDate = current?.endDate ?: LocalDate.now().plusMonths(5),
            isActive = current?.active ?: true
        )
    }

    fun dismissSemesterSheet() {
        _semesterSheetState.update { it.copy(isVisible = false) }
    }

    fun updateSemesterName(name: String) = _semesterSheetState.update { it.copy(name = name) }
    fun updateSemesterDates(start: LocalDate, end: LocalDate) =
        _semesterSheetState.update { it.copy(startDate = start, endDate = end) }
    fun updateSemesterActive(active: Boolean) = _semesterSheetState.update { it.copy(isActive = active) }

    fun saveSemester() {
        val state = _semesterSheetState.value
        val nameTrimmed = state.name.trim().ifEmpty { "Semester Baru" }

        viewModelScope.launch {
            val entity = SemesterEntity(
                id = state.semesterId ?: 0L,
                name = nameTrimmed,
                startDate = state.startDate,
                endDate = state.endDate,
                active = state.isActive
            )
            scheduleRepository.saveSemester(entity)
            _semesterSheetState.value = AddEditSemesterState(isVisible = false)
        }
    }
}
