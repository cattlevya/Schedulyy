package com.pemmob.schedulyy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pemmob.schedulyy.domain.model.EventType
import com.pemmob.schedulyy.domain.model.ProkerStatus
import com.pemmob.schedulyy.domain.model.ReminderTargetType
import com.pemmob.schedulyy.domain.model.SpaceType
import com.pemmob.schedulyy.domain.model.TaskPriority
import com.pemmob.schedulyy.domain.model.TaskSource
import com.pemmob.schedulyy.domain.model.TaskStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "spaces",
    foreignKeys = [
        ForeignKey(
            entity = SpaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["parentId"]),
        Index(value = ["archived"])
    ]
)
data class SpaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parentId: Long? = null,
    val type: SpaceType,
    val name: String,
    val colorKey: String,
    val iconKey: String,
    val archived: Boolean = false,
    val createdAt: Instant = Instant.now()
)

@Entity(tableName = "semesters")
data class SemesterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val active: Boolean = true
)

@Entity(
    tableName = "courses",
    foreignKeys = [
        ForeignKey(
            entity = SpaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["spaceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["id"],
            childColumns = ["semesterId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["spaceId"]),
        Index(value = ["semesterId"])
    ]
)
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val spaceId: Long,
    val semesterId: Long? = null,
    val name: String,
    val code: String? = null,
    val lecturer: String? = null,
    val room: String? = null,
    val sks: Int? = null,
    val colorKey: String,
    val iconKey: String,
    val link: String? = null
)

@Entity(
    tableName = "class_sessions",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["courseId"]),
        Index(value = ["dayOfWeek"])
    ]
)
data class ClassSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Long,
    val dayOfWeek: Int, // 1 = Senin ... 7 = Minggu
    val startTime: LocalTime,
    val endTime: LocalTime,
    val room: String? = null
)

@Entity(
    tableName = "divisions",
    foreignKeys = [
        ForeignKey(
            entity = SpaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["spaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["spaceId"])
    ]
)
data class DivisionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val spaceId: Long,
    val name: String,
    val colorKey: String? = null
)

@Entity(
    tableName = "prokers",
    foreignKeys = [
        ForeignKey(
            entity = SpaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["spaceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DivisionEntity::class,
            parentColumns = ["id"],
            childColumns = ["divisionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["spaceId"]),
        Index(value = ["divisionId"]),
        Index(value = ["status"])
    ]
)
data class ProkerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val spaceId: Long,
    val divisionId: Long? = null,
    val name: String,
    val description: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val status: ProkerStatus = ProkerStatus.RENCANA
)

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = SpaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["spaceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProkerEntity::class,
            parentColumns = ["id"],
            childColumns = ["prokerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["spaceId"]),
        Index(value = ["prokerId"]),
        Index(value = ["start"]),
        Index(value = ["type"])
    ]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val spaceId: Long,
    val prokerId: Long? = null,
    val title: String,
    val start: Instant,
    val end: Instant? = null,
    val location: String? = null,
    val type: EventType = EventType.LAINNYA,
    val notes: String? = null,
    val repeatWeekly: Boolean = false
)

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = SpaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["spaceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ProkerEntity::class,
            parentColumns = ["id"],
            childColumns = ["prokerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["spaceId"]),
        Index(value = ["courseId"]),
        Index(value = ["prokerId"]),
        Index(value = ["deadline"]),
        Index(value = ["status"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val spaceId: Long,
    val courseId: Long? = null,
    val prokerId: Long? = null,
    val title: String,
    val notes: String? = null,
    val deadline: Instant? = null,
    val hasTime: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val source: TaskSource = TaskSource.MANUAL,
    val assigneeLabel: String? = null,
    val createdAt: Instant = Instant.now(),
    val completedAt: Instant? = null
)

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["taskId"])
    ]
)
data class SubTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val title: String,
    val done: Boolean = false,
    val position: Int = 0
)

@Entity(
    tableName = "reminders",
    indices = [
        Index(value = ["targetType", "targetId"]),
        Index(value = ["triggerAt"])
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: ReminderTargetType,
    val targetId: Long,
    val triggerAt: Instant,
    val offsetMinutes: Int
)
