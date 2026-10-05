package com.pemmob.schedulyy.domain.model

import com.pemmob.schedulyy.data.local.dao.TaskWithSubTasks
import com.pemmob.schedulyy.data.local.entity.SubTaskEntity
import com.pemmob.schedulyy.data.local.entity.TaskEntity
import com.pemmob.schedulyy.domain.util.DeadlineGroupUtil
import java.time.Instant

data class SubTaskUiModel(
    val id: Long,
    val title: String,
    val done: Boolean,
    val position: Int
)

data class TaskUiModel(
    val id: Long,
    val spaceId: Long?,
    val courseId: Long?,
    val title: String,
    val notes: String?,
    val deadline: Instant?,
    val hasTime: Boolean,
    val priority: TaskPriority,
    val status: TaskStatus,
    val source: TaskSource,
    val assigneeLabel: String?,
    val subTasks: List<SubTaskUiModel>,
    val deadlineGroup: DeadlineGroup,
    val createdAt: Instant,
    val completedAt: Instant?
) {
    val isDone: Boolean get() = status == TaskStatus.DONE
    val subTaskProgress: Float
        get() = if (subTasks.isEmpty()) 0f
                else subTasks.count { it.done }.toFloat() / subTasks.size
}

fun TaskWithSubTasks.toUiModel(now: Instant = Instant.now()): TaskUiModel {
    val group = DeadlineGroupUtil.classify(
        deadline = task.deadline,
        hasTime = task.hasTime,
        now = now
    )
    return TaskUiModel(
        id = task.id,
        spaceId = task.spaceId,
        courseId = task.courseId,
        title = task.title,
        notes = task.notes,
        deadline = task.deadline,
        hasTime = task.hasTime,
        priority = task.priority,
        status = task.status,
        source = task.source,
        assigneeLabel = task.assigneeLabel,
        deadlineGroup = group,
        createdAt = task.createdAt,
        completedAt = task.completedAt,
        subTasks = subTasks.map { sub ->
            SubTaskUiModel(
                id = sub.id,
                title = sub.title,
                done = sub.done,
                position = sub.position
            )
        }
    )
}

fun TaskUiModel.toEntity(): TaskEntity = TaskEntity(
    id = id,
    spaceId = spaceId ?: 1L, // default Space Pribadi
    courseId = courseId,
    prokerId = null,
    title = title,
    notes = notes,
    deadline = deadline,
    hasTime = hasTime,
    priority = priority,
    status = status,
    source = source,
    assigneeLabel = assigneeLabel,
    createdAt = createdAt,
    completedAt = completedAt
)

fun SubTaskUiModel.toEntity(taskId: Long): SubTaskEntity = SubTaskEntity(
    id = id,
    taskId = taskId,
    title = title,
    done = done,
    position = position
)
