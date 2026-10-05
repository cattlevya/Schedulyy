package com.pemmob.schedulyy.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import com.pemmob.schedulyy.data.local.entity.SubTaskEntity
import com.pemmob.schedulyy.data.local.entity.TaskEntity
import com.pemmob.schedulyy.domain.model.DeadlineGroup
import com.pemmob.schedulyy.domain.model.SpaceType
import com.pemmob.schedulyy.domain.model.SubTaskUiModel
import com.pemmob.schedulyy.domain.model.TaskPriority
import com.pemmob.schedulyy.domain.model.TaskSource
import com.pemmob.schedulyy.domain.model.TaskStatus
import com.pemmob.schedulyy.domain.model.TaskUiModel
import com.pemmob.schedulyy.domain.model.toEntity
import com.pemmob.schedulyy.domain.model.toUiModel
import com.pemmob.schedulyy.domain.repository.SpaceRepository
import com.pemmob.schedulyy.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

import com.pemmob.schedulyy.data.notification.ReminderScheduler

// ─── UI State ────────────────────────────────────────────────────────────────

data class TasksUiState(
    val groupedTasks: Map<DeadlineGroup, List<TaskUiModel>> = emptyMap(),
    val spaces: List<SpaceEntity> = emptyList(),
    val filterSpaceId: Long? = null,   // null = semua
    val query: String = "",
    val isLoading: Boolean = true,
    val undoTask: TaskUiModel? = null  // untuk snackbar undo
)

data class AddEditTaskState(
    val isVisible: Boolean = false,
    val editingTask: TaskUiModel? = null,  // null = mode tambah baru
    val title: String = "",
    val notes: String = "",
    val spaceId: Long = 1L,
    val deadline: Instant? = null,
    val hasTime: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val subTasks: List<SubTaskUiModel> = emptyList(),
    val isSaving: Boolean = false
)

data class AddSpaceState(
    val isVisible: Boolean = false,
    val name: String = "",
    val type: SpaceType = SpaceType.PRIBADI,
    val colorKey: String = "pastelButter"
)

// ─── ViewModel ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepo: TaskRepository,
    private val spaceRepo: SpaceRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _filterSpaceId = MutableStateFlow<Long?>(null)
    private val _query = MutableStateFlow("")
    private val _undoTask = MutableStateFlow<TaskUiModel?>(null)

    /** Sheet tambah/edit tugas */
    val sheetState = MutableStateFlow(AddEditTaskState())

    /** Sheet tambah Space/Folder baru */
    val addSpaceState = MutableStateFlow(AddSpaceState())

    val uiState: StateFlow<TasksUiState> = combine(
        _filterSpaceId.flatMapLatest { spaceId ->
            if (spaceId == null) {
                taskRepo.getAllTasksWithSubTasks()
            } else {
                taskRepo.getAllTasksWithSubTasks().map { list ->
                    list.filter { it.task.spaceId == spaceId }
                }
            }
        },
        _query.debounce(200),
        spaceRepo.getAllActiveSpaces(),
        _undoTask
    ) { withSubTasks, query, spaces, undo ->
        val now = Instant.now()
        val filtered = withSubTasks
            .map { it.toUiModel(now) }
            .filter { task ->
                if (query.isBlank()) true
                else task.title.contains(query, ignoreCase = true) ||
                        task.notes?.contains(query, ignoreCase = true) == true
            }
            .filter { it.status != TaskStatus.DONE || it.deadlineGroup == DeadlineGroup.TODAY }

        val grouped = linkedMapOf<DeadlineGroup, MutableList<TaskUiModel>>()
        DeadlineGroup.entries.forEach { group -> grouped[group] = mutableListOf() }
        filtered.forEach { task -> grouped[task.deadlineGroup]?.add(task) }

        TasksUiState(
            groupedTasks = grouped.filter { it.value.isNotEmpty() },
            spaces = spaces,
            filterSpaceId = _filterSpaceId.value,
            query = query,
            isLoading = false,
            undoTask = undo
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TasksUiState())

    // ─── Filter & Search ─────────────────────────────────────────────────────

    fun setFilterSpace(spaceId: Long?) {
        _filterSpaceId.value = spaceId
    }

    fun setQuery(q: String) {
        _query.value = q
    }

    // ─── Task Actions ─────────────────────────────────────────────────────────

    fun toggleDone(task: TaskUiModel) {
        viewModelScope.launch {
            val newStatus = if (task.isDone) TaskStatus.TODO else TaskStatus.DONE
            taskRepo.setTaskStatus(task.id, newStatus)
            if (newStatus == TaskStatus.DONE) {
                _undoTask.value = task
            }
            reminderScheduler.scheduleAll()
        }
    }

    fun undoComplete() {
        viewModelScope.launch {
            _undoTask.value?.let { task ->
                taskRepo.setTaskStatus(task.id, TaskStatus.TODO)
                _undoTask.value = null
                reminderScheduler.scheduleAll()
            }
        }
    }

    fun dismissUndo() {
        _undoTask.value = null
    }

    fun deleteTask(task: TaskUiModel) {
        viewModelScope.launch {
            taskRepo.deleteTask(task.toEntity())
            reminderScheduler.scheduleAll()
        }
    }

    // ─── Add/Edit Sheet ───────────────────────────────────────────────────────

    fun openAddSheet(spaceId: Long = _filterSpaceId.value ?: 1L) {
        sheetState.value = AddEditTaskState(
            isVisible = true,
            spaceId = spaceId
        )
    }

    fun openEditSheet(task: TaskUiModel) {
        sheetState.value = AddEditTaskState(
            isVisible = true,
            editingTask = task,
            title = task.title,
            notes = task.notes ?: "",
            spaceId = task.spaceId ?: 1L,
            deadline = task.deadline,
            hasTime = task.hasTime,
            priority = task.priority,
            subTasks = task.subTasks
        )
    }

    fun closeSheet() {
        sheetState.value = AddEditTaskState()
    }

    fun updateSheetTitle(v: String)  { sheetState.value = sheetState.value.copy(title = v) }
    fun updateSheetNotes(v: String)  { sheetState.value = sheetState.value.copy(notes = v) }
    fun updateSheetSpace(id: Long)   { sheetState.value = sheetState.value.copy(spaceId = id) }
    fun updateSheetDeadline(i: Instant?) { sheetState.value = sheetState.value.copy(deadline = i) }
    fun updateSheetHasTime(v: Boolean)  { sheetState.value = sheetState.value.copy(hasTime = v) }
    fun updateSheetPriority(p: TaskPriority) { sheetState.value = sheetState.value.copy(priority = p) }

    fun addSubTask() {
        val current = sheetState.value.subTasks.toMutableList()
        current.add(SubTaskUiModel(id = 0, title = "", done = false, position = current.size))
        sheetState.value = sheetState.value.copy(subTasks = current)
    }

    fun updateSubTask(index: Int, title: String) {
        val current = sheetState.value.subTasks.toMutableList()
        if (index < current.size) {
            current[index] = current[index].copy(title = title)
            sheetState.value = sheetState.value.copy(subTasks = current)
        }
    }

    fun removeSubTask(index: Int) {
        val current = sheetState.value.subTasks.toMutableList()
        if (index < current.size) {
            current.removeAt(index)
            sheetState.value = sheetState.value.copy(subTasks = current)
        }
    }

    fun saveTask() {
        val s = sheetState.value
        if (s.title.isBlank()) return
        sheetState.value = s.copy(isSaving = true)

        viewModelScope.launch {
            val editId = s.editingTask?.id ?: 0L
            val entity = TaskEntity(
                id = editId,
                spaceId = s.spaceId,
                title = s.title.trim(),
                notes = s.notes.trim().ifBlank { null },
                deadline = s.deadline,
                hasTime = s.hasTime,
                priority = s.priority,
                status = s.editingTask?.status ?: TaskStatus.TODO,
                source = TaskSource.MANUAL,
                createdAt = s.editingTask?.createdAt ?: Instant.now()
            )
            val subEntities = s.subTasks
                .filter { it.title.isNotBlank() }
                .mapIndexed { i, sub -> SubTaskEntity(
                    id = sub.id,
                    taskId = editId,
                    title = sub.title.trim(),
                    done = sub.done,
                    position = i
                )}

            if (editId == 0L) {
                taskRepo.insertTask(entity, subEntities)
            } else {
                taskRepo.updateTask(entity)
                // subtugas: hapus semua lama, insert baru
                subEntities.forEach { taskRepo.insertTask(entity, listOf(it)) }
            }
            reminderScheduler.scheduleAll()
            sheetState.value = AddEditTaskState()
        }
    }

    // ─── Space / Folder Management ────────────────────────────────────────────

    fun openSpaceSheet() {
        addSpaceState.value = AddSpaceState(isVisible = true)
    }

    fun closeSpaceSheet() {
        addSpaceState.value = AddSpaceState()
    }

    fun updateSpaceType(type: SpaceType) {
        addSpaceState.value = addSpaceState.value.copy(type = type)
    }

    fun updateSpaceName(name: String) {
        addSpaceState.value = addSpaceState.value.copy(name = name)
    }

    fun updateSpaceColor(colorKey: String) {
        addSpaceState.value = addSpaceState.value.copy(colorKey = colorKey)
    }

    fun saveSpace() {
        val s = addSpaceState.value
        if (s.name.isBlank()) return
        viewModelScope.launch {
            val newId = spaceRepo.insertSpace(
                SpaceEntity(
                    type = s.type,
                    name = s.name.trim(),
                    colorKey = s.colorKey,
                    iconKey = when (s.type) {
                        SpaceType.KULIAH      -> "school"
                        SpaceType.ORGANISASI  -> "groups"
                        SpaceType.KEPANITIAAN -> "event"
                        SpaceType.PRIBADI     -> "person"
                        SpaceType.CUSTOM      -> "star"
                    }
                )
            )
            // Otomatis filter ke Space baru setelah dibuat
            _filterSpaceId.value = newId
            addSpaceState.value = AddSpaceState()
        }
    }
}

