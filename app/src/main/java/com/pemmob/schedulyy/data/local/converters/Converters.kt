package com.pemmob.schedulyy.data.local.converters

import androidx.room.TypeConverter
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
import java.time.format.DateTimeFormatter

class Converters {

    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.format(dateFormatter)

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it, dateFormatter) }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.format(timeFormatter)

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it, timeFormatter) }

    @TypeConverter
    fun fromSpaceType(value: SpaceType?): String? = value?.name

    @TypeConverter
    fun toSpaceType(value: String?): SpaceType? = value?.let { enumValueOf<SpaceType>(it) }

    @TypeConverter
    fun fromProkerStatus(value: ProkerStatus?): String? = value?.name

    @TypeConverter
    fun toProkerStatus(value: String?): ProkerStatus? = value?.let { enumValueOf<ProkerStatus>(it) }

    @TypeConverter
    fun fromEventType(value: EventType?): String? = value?.name

    @TypeConverter
    fun toEventType(value: String?): EventType? = value?.let { enumValueOf<EventType>(it) }

    @TypeConverter
    fun fromTaskPriority(value: TaskPriority?): String? = value?.name

    @TypeConverter
    fun toTaskPriority(value: String?): TaskPriority? = value?.let { enumValueOf<TaskPriority>(it) }

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus?): String? = value?.name

    @TypeConverter
    fun toTaskStatus(value: String?): TaskStatus? = value?.let { enumValueOf<TaskStatus>(it) }

    @TypeConverter
    fun fromTaskSource(value: TaskSource?): String? = value?.name

    @TypeConverter
    fun toTaskSource(value: String?): TaskSource? = value?.let { enumValueOf<TaskSource>(it) }

    @TypeConverter
    fun fromReminderTargetType(value: ReminderTargetType?): String? = value?.name

    @TypeConverter
    fun toReminderTargetType(value: String?): ReminderTargetType? = value?.let { enumValueOf<ReminderTargetType>(it) }
}
