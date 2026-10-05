package com.pemmob.schedulyy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
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
import com.pemmob.schedulyy.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.time.Instant

// Relation Data Classes
data class CourseWithSessions(
    @Embedded val course: CourseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "courseId"
    )
    val sessions: List<ClassSessionEntity>
)

data class TaskWithSubTasks(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subTasks: List<SubTaskEntity>
)

@Dao
interface SpaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(space: SpaceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(spaces: List<SpaceEntity>): List<Long>

    @Update
    suspend fun update(space: SpaceEntity)

    @Query("UPDATE spaces SET archived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)

    @Delete
    suspend fun delete(space: SpaceEntity)

    @Query("SELECT * FROM spaces WHERE archived = 0 ORDER BY createdAt DESC")
    fun getAllActiveSpaces(): Flow<List<SpaceEntity>>

    @Query("SELECT * FROM spaces WHERE id = :id")
    suspend fun getSpaceById(id: Long): SpaceEntity?

    @Query("SELECT * FROM spaces WHERE parentId = :parentId AND archived = 0")
    fun getChildSpaces(parentId: Long): Flow<List<SpaceEntity>>

    @Query("SELECT * FROM spaces WHERE parentId IS NULL AND archived = 0 ORDER BY createdAt DESC")
    fun getRootSpaces(): Flow<List<SpaceEntity>>

    @Query("SELECT * FROM spaces")
    suspend fun getAllSpacesSync(): List<SpaceEntity>
}

@Dao
interface SemesterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(semester: SemesterEntity): Long

    @Update
    suspend fun update(semester: SemesterEntity)

    @Delete
    suspend fun delete(semester: SemesterEntity)

    @Query("SELECT * FROM semesters WHERE active = 1 LIMIT 1")
    fun getActiveSemester(): Flow<SemesterEntity?>

    @Query("SELECT * FROM semesters ORDER BY startDate DESC")
    fun getAllSemesters(): Flow<List<SemesterEntity>>

    @Query("UPDATE semesters SET active = 0")
    suspend fun deactivateAll()

    @Query("DELETE FROM semesters WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM semesters")
    suspend fun getAllSemestersSync(): List<SemesterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(semesters: List<SemesterEntity>): List<Long>
}

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: CourseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>): List<Long>

    @Update
    suspend fun update(course: CourseEntity)

    @Delete
    suspend fun delete(course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM courses WHERE spaceId = :spaceId")
    fun getCoursesBySpace(spaceId: Long): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: Long): CourseEntity?

    @Transaction
    @Query("SELECT * FROM courses")
    fun getAllCoursesWithSessions(): Flow<List<CourseWithSessions>>

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseWithSessions(id: Long): CourseWithSessions?

    @Query("SELECT * FROM courses")
    suspend fun getAllCoursesSync(): List<CourseEntity>
}

@Dao
interface ClassSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ClassSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<ClassSessionEntity>): List<Long>

    @Update
    suspend fun update(session: ClassSessionEntity)

    @Delete
    suspend fun delete(session: ClassSessionEntity)

    @Query("DELETE FROM class_sessions WHERE courseId = :courseId")
    suspend fun deleteByCourseId(courseId: Long)

    @Query("SELECT * FROM class_sessions WHERE courseId = :courseId")
    fun getSessionsByCourse(courseId: Long): Flow<List<ClassSessionEntity>>

    @Query("SELECT * FROM class_sessions WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getSessionsByDay(dayOfWeek: Int): Flow<List<ClassSessionEntity>>

    @Query("SELECT * FROM class_sessions ORDER BY dayOfWeek, startTime ASC")
    fun getAllSessions(): Flow<List<ClassSessionEntity>>

    @Query("SELECT * FROM class_sessions")
    suspend fun getAllSessionsSync(): List<ClassSessionEntity>
}

@Dao
interface DivisionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(division: DivisionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(divisions: List<DivisionEntity>): List<Long>

    @Update
    suspend fun update(division: DivisionEntity)

    @Delete
    suspend fun delete(division: DivisionEntity)

    @Query("SELECT * FROM divisions WHERE spaceId = :spaceId")
    fun getDivisionsBySpace(spaceId: Long): Flow<List<DivisionEntity>>

    @Query("SELECT * FROM divisions")
    suspend fun getAllDivisionsSync(): List<DivisionEntity>
}

@Dao
interface ProkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(proker: ProkerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prokers: List<ProkerEntity>): List<Long>

    @Update
    suspend fun update(proker: ProkerEntity)

    @Delete
    suspend fun delete(proker: ProkerEntity)

    @Query("SELECT * FROM prokers WHERE spaceId = :spaceId")
    fun getProkersBySpace(spaceId: Long): Flow<List<ProkerEntity>>

    @Query("SELECT * FROM prokers WHERE divisionId = :divisionId")
    fun getProkersByDivision(divisionId: Long): Flow<List<ProkerEntity>>

    @Query("SELECT * FROM prokers")
    suspend fun getAllProkersSync(): List<ProkerEntity>
}

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>): List<Long>

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)

    @Query("SELECT * FROM events WHERE spaceId = :spaceId ORDER BY start ASC")
    fun getEventsBySpace(spaceId: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY start ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE start >= :fromInstant AND start <= :toInstant ORDER BY start ASC")
    fun getEventsInRange(fromInstant: Instant, toInstant: Instant): Flow<List<EventEntity>>

    @Query("SELECT * FROM events")
    suspend fun getAllEventsSync(): List<EventEntity>
}

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>): List<Long>

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("UPDATE tasks SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: TaskStatus, completedAt: Instant?)

    @Query("SELECT * FROM tasks ORDER BY deadline ASC NULLS LAST")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE spaceId = :spaceId ORDER BY deadline ASC NULLS LAST")
    fun getTasksBySpace(spaceId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY deadline ASC NULLS LAST")
    fun getAllTasksWithSubTasks(): Flow<List<TaskWithSubTasks>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskWithSubTasks(id: Long): TaskWithSubTasks?

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksSync(): List<TaskEntity>
}

@Dao
interface SubTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subTask: SubTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subTasks: List<SubTaskEntity>): List<Long>

    @Update
    suspend fun update(subTask: SubTaskEntity)

    @Delete
    suspend fun delete(subTask: SubTaskEntity)

    @Query("UPDATE subtasks SET done = :done WHERE id = :id")
    suspend fun updateDone(id: Long, done: Boolean)

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY position ASC")
    fun getSubTasksByTaskId(taskId: Long): Flow<List<SubTaskEntity>>

    @Query("SELECT * FROM subtasks")
    suspend fun getAllSubTasksSync(): List<SubTaskEntity>
}

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE triggerAt >= :now ORDER BY triggerAt ASC")
    fun getUpcomingReminders(now: Instant): Flow<List<ReminderEntity>>
}
