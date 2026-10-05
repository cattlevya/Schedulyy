package com.pemmob.schedulyy.ui.spaces

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.schedulyy.data.local.entity.DivisionEntity
import com.pemmob.schedulyy.data.local.entity.EventEntity
import com.pemmob.schedulyy.data.local.entity.ProkerEntity
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import com.pemmob.schedulyy.data.local.entity.TaskEntity
import com.pemmob.schedulyy.domain.model.EventType
import com.pemmob.schedulyy.domain.model.ProkerStatus
import com.pemmob.schedulyy.domain.model.TaskPriority
import com.pemmob.schedulyy.domain.model.TaskStatus
import com.pemmob.schedulyy.domain.repository.SpaceRepository
import com.pemmob.schedulyy.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import com.pemmob.schedulyy.data.notification.ReminderScheduler

enum class SpaceTab {
    SUMMARY,
    PROKER,
    TASKS,
    SCHEDULE
}

data class AddDivisionState(
    val isVisible: Boolean = false,
    val name: String = "",
    val colorKey: String = "mint"
)

data class AddProkerState(
    val isVisible: Boolean = false,
    val prokerId: Long? = null,
    val name: String = "",
    val description: String = "",
    val divisionId: Long? = null,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusMonths(1),
    val status: ProkerStatus = ProkerStatus.RENCANA,
    val errorMessage: String? = null
)

data class AddEventState(
    val isVisible: Boolean = false,
    val title: String = "",
    val type: EventType = EventType.RAPAT,
    val location: String = "",
    val start: Instant = Instant.now(),
    val end: Instant = Instant.now().plus(2, ChronoUnit.HOURS),
    val prokerId: Long? = null,
    val notes: String = "",
    val repeatWeekly: Boolean = false,
    val errorMessage: String? = null
)

data class AddSpaceTaskState(
    val isVisible: Boolean = false,
    val title: String = "",
    val notes: String = "",
    val prokerId: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val deadline: Instant? = null
)

data class SpaceDetailUiState(
    val space: SpaceEntity? = null,
    val childSpaces: List<SpaceEntity> = emptyList(),
    val divisions: List<DivisionEntity> = emptyList(),
    val prokers: List<ProkerEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val events: List<EventEntity> = emptyList(),
    val selectedTab: SpaceTab = SpaceTab.SUMMARY,
    val selectedDivisionFilter: Long? = null,
    val selectedProkerStatusFilter: ProkerStatus? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class SpaceDetailViewModel @Inject constructor(
    private val spaceRepository: SpaceRepository,
    private val taskRepository: TaskRepository,
    private val reminderScheduler: ReminderScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val spaceId: Long = checkNotNull(savedStateHandle["spaceId"])

    private val _uiState = MutableStateFlow(SpaceDetailUiState(isLoading = true))
    val uiState: StateFlow<SpaceDetailUiState> = _uiState.asStateFlow()

    private val _divisionSheetState = MutableStateFlow(AddDivisionState())
    val divisionSheetState: StateFlow<AddDivisionState> = _divisionSheetState.asStateFlow()

    private val _prokerSheetState = MutableStateFlow(AddProkerState())
    val prokerSheetState: StateFlow<AddProkerState> = _prokerSheetState.asStateFlow()

    private val _eventSheetState = MutableStateFlow(AddEventState())
    val eventSheetState: StateFlow<AddEventState> = _eventSheetState.asStateFlow()

    private val _taskSheetState = MutableStateFlow(AddSpaceTaskState())
    val taskSheetState: StateFlow<AddSpaceTaskState> = _taskSheetState.asStateFlow()

    init {
        loadSpaceDetails()
    }

    private fun loadSpaceDetails() {
        viewModelScope.launch {
            val space = spaceRepository.getSpaceById(spaceId)

            combine(
                spaceRepository.getChildSpaces(spaceId),
                spaceRepository.getDivisionsBySpace(spaceId),
                spaceRepository.getProkersBySpace(spaceId),
                taskRepository.getTasksBySpace(spaceId),
                spaceRepository.getEventsBySpace(spaceId)
            ) { children, divisions, prokers, tasks, events ->
                _uiState.value.copy(
                    space = space,
                    childSpaces = children,
                    divisions = divisions,
                    prokers = prokers,
                    tasks = tasks,
                    events = events,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectTab(tab: SpaceTab) = _uiState.update { it.copy(selectedTab = tab) }
    fun filterDivision(divisionId: Long?) = _uiState.update { it.copy(selectedDivisionFilter = divisionId) }
    fun filterProkerStatus(status: ProkerStatus?) = _uiState.update { it.copy(selectedProkerStatusFilter = status) }

    // ─── Divisi Sheet ─────────────────────────────────────────────────────────

    fun openAddDivision() {
        _divisionSheetState.value = AddDivisionState(isVisible = true)
    }

    fun dismissAddDivision() {
        _divisionSheetState.update { it.copy(isVisible = false) }
    }

    fun updateDivisionName(name: String) = _divisionSheetState.update { it.copy(name = name) }
    fun updateDivisionColor(color: String) = _divisionSheetState.update { it.copy(colorKey = color) }

    fun saveDivision() {
        val state = _divisionSheetState.value
        val nameTrimmed = state.name.trim()
        if (nameTrimmed.isEmpty()) return

        viewModelScope.launch {
            spaceRepository.insertDivision(
                DivisionEntity(
                    spaceId = spaceId,
                    name = nameTrimmed,
                    colorKey = state.colorKey
                )
            )
            _divisionSheetState.value = AddDivisionState(isVisible = false)
        }
    }

    // ─── Proker Sheet ─────────────────────────────────────────────────────────

    fun openAddProker(divisionId: Long? = null) {
        _prokerSheetState.value = AddProkerState(
            isVisible = true,
            divisionId = divisionId
        )
    }

    fun openEditProker(proker: ProkerEntity) {
        _prokerSheetState.value = AddProkerState(
            isVisible = true,
            prokerId = proker.id,
            name = proker.name,
            description = proker.description.orEmpty(),
            divisionId = proker.divisionId,
            startDate = proker.startDate ?: LocalDate.now(),
            endDate = proker.endDate ?: LocalDate.now().plusMonths(1),
            status = proker.status
        )
    }

    fun dismissAddProker() {
        _prokerSheetState.update { it.copy(isVisible = false) }
    }

    fun updateProkerName(name: String) = _prokerSheetState.update { it.copy(name = name, errorMessage = null) }
    fun updateProkerDesc(desc: String) = _prokerSheetState.update { it.copy(description = desc) }
    fun updateProkerDivision(divId: Long?) = _prokerSheetState.update { it.copy(divisionId = divId) }
    fun updateProkerDates(start: LocalDate, end: LocalDate) = _prokerSheetState.update { it.copy(startDate = start, endDate = end) }
    fun updateProkerStatus(status: ProkerStatus) = _prokerSheetState.update { it.copy(status = status) }

    fun cycleProkerStatus(proker: ProkerEntity) {
        val nextStatus = when (proker.status) {
            ProkerStatus.RENCANA  -> ProkerStatus.BERJALAN
            ProkerStatus.BERJALAN -> ProkerStatus.SELESAI
            ProkerStatus.SELESAI  -> ProkerStatus.RENCANA
        }
        viewModelScope.launch {
            spaceRepository.updateProker(proker.copy(status = nextStatus))
        }
    }

    fun saveProker() {
        val state = _prokerSheetState.value
        val nameTrimmed = state.name.trim()
        if (nameTrimmed.isEmpty()) {
            _prokerSheetState.update { it.copy(errorMessage = "Nama proker tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            val entity = ProkerEntity(
                id = state.prokerId ?: 0L,
                spaceId = spaceId,
                divisionId = state.divisionId,
                name = nameTrimmed,
                description = state.description.trim().takeIf { it.isNotEmpty() },
                startDate = state.startDate,
                endDate = state.endDate,
                status = state.status
            )
            if (state.prokerId == null || state.prokerId == 0L) {
                spaceRepository.insertProker(entity)
            } else {
                spaceRepository.updateProker(entity)
            }
            _prokerSheetState.value = AddProkerState(isVisible = false)
        }
    }

    // ─── Event (Kegiatan) Sheet ───────────────────────────────────────────────

    fun openAddEvent(prokerId: Long? = null) {
        _eventSheetState.value = AddEventState(
            isVisible = true,
            prokerId = prokerId
        )
    }

    fun dismissAddEvent() {
        _eventSheetState.update { it.copy(isVisible = false) }
    }

    fun updateEventTitle(title: String) = _eventSheetState.update { it.copy(title = title, errorMessage = null) }
    fun updateEventType(type: EventType) = _eventSheetState.update { it.copy(type = type) }
    fun updateEventLocation(loc: String) = _eventSheetState.update { it.copy(location = loc) }
    fun updateEventTimes(start: Instant, end: Instant) = _eventSheetState.update { it.copy(start = start, end = end) }
    fun updateEventProker(prokerId: Long?) = _eventSheetState.update { it.copy(prokerId = prokerId) }
    fun updateEventNotes(notes: String) = _eventSheetState.update { it.copy(notes = notes) }
    fun updateEventRepeatWeekly(repeat: Boolean) = _eventSheetState.update { it.copy(repeatWeekly = repeat) }

    fun saveEvent() {
        val state = _eventSheetState.value
        val titleTrimmed = state.title.trim()
        if (titleTrimmed.isEmpty()) {
            _eventSheetState.update { it.copy(errorMessage = "Judul kegiatan wajib diisi") }
            return
        }

        viewModelScope.launch {
            val entity = EventEntity(
                spaceId = spaceId,
                prokerId = state.prokerId,
                title = titleTrimmed,
                start = state.start,
                end = state.end,
                location = state.location.trim().takeIf { it.isNotEmpty() },
                type = state.type,
                notes = state.notes.trim().takeIf { it.isNotEmpty() },
                repeatWeekly = state.repeatWeekly
            )
            spaceRepository.insertEvent(entity)
            reminderScheduler.scheduleAll()
            _eventSheetState.value = AddEventState(isVisible = false)
        }
    }

    // ─── Task Sheet in Space ──────────────────────────────────────────────────

    fun openAddTask(prokerId: Long? = null) {
        _taskSheetState.value = AddSpaceTaskState(
            isVisible = true,
            prokerId = prokerId
        )
    }

    fun dismissAddTask() {
        _taskSheetState.update { it.copy(isVisible = false) }
    }

    fun updateTaskTitle(title: String) = _taskSheetState.update { it.copy(title = title) }
    fun updateTaskProker(prokerId: Long?) = _taskSheetState.update { it.copy(prokerId = prokerId) }

    fun saveTask() {
        val state = _taskSheetState.value
        val titleTrimmed = state.title.trim()
        if (titleTrimmed.isEmpty()) return

        viewModelScope.launch {
            val entity = TaskEntity(
                spaceId = spaceId,
                prokerId = state.prokerId,
                title = titleTrimmed,
                notes = state.notes.trim().takeIf { it.isNotEmpty() },
                priority = state.priority,
                deadline = state.deadline
            )
            taskRepository.insertTask(entity)
            reminderScheduler.scheduleAll()
            _taskSheetState.value = AddSpaceTaskState(isVisible = false)
        }
    }

    fun toggleTaskDone(task: TaskEntity) {
        viewModelScope.launch {
            val nextStatus = if (task.status == TaskStatus.DONE) TaskStatus.TODO else TaskStatus.DONE
            taskRepository.setTaskStatus(task.id, nextStatus)
            reminderScheduler.scheduleAll()
        }
    }
}
