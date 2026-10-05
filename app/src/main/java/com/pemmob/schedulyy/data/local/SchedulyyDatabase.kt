package com.pemmob.schedulyy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pemmob.schedulyy.data.local.converters.Converters
import com.pemmob.schedulyy.data.local.dao.ClassSessionDao
import com.pemmob.schedulyy.data.local.dao.CourseDao
import com.pemmob.schedulyy.data.local.dao.DivisionDao
import com.pemmob.schedulyy.data.local.dao.EventDao
import com.pemmob.schedulyy.data.local.dao.ProkerDao
import com.pemmob.schedulyy.data.local.dao.ReminderDao
import com.pemmob.schedulyy.data.local.dao.SemesterDao
import com.pemmob.schedulyy.data.local.dao.SpaceDao
import com.pemmob.schedulyy.data.local.dao.SubTaskDao
import com.pemmob.schedulyy.data.local.dao.TaskDao
import com.pemmob.schedulyy.data.local.entity.ClassSessionEntity
import com.pemmob.schedulyy.data.local.entity.CourseEntity
import com.pemmob.schedulyy.data.local.entity.DivisionEntity
import com.pemmob.schedulyy.data.local.entity.EventEntity
import com.pemmob.schedulyy.data.local.entity.ProkerEntity
import com.pemmob.schedulyy.data.local.entity.ReminderEntity
import com.pemmob.schedulyy.data.local.entity.SemesterEntity
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import com.pemmob.schedulyy.data.local.entity.SubTaskEntity
import com.pemmob.schedulyy.data.local.entity.TaskEntity

@Database(
    entities = [
        SpaceEntity::class,
        SemesterEntity::class,
        CourseEntity::class,
        ClassSessionEntity::class,
        DivisionEntity::class,
        ProkerEntity::class,
        EventEntity::class,
        TaskEntity::class,
        SubTaskEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SchedulyyDatabase : RoomDatabase() {
    abstract fun spaceDao(): SpaceDao
    abstract fun semesterDao(): SemesterDao
    abstract fun courseDao(): CourseDao
    abstract fun classSessionDao(): ClassSessionDao
    abstract fun divisionDao(): DivisionDao
    abstract fun prokerDao(): ProkerDao
    abstract fun eventDao(): EventDao
    abstract fun taskDao(): TaskDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "schedulyy.db"
    }
}
