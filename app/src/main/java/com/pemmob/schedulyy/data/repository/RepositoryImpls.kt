package com.pemmob.schedulyy.data.repository

import com.pemmob.schedulyy.data.local.dao.ClassSessionDao
import com.pemmob.schedulyy.data.local.dao.CourseDao
import com.pemmob.schedulyy.data.local.dao.CourseWithSessions
import com.pemmob.schedulyy.data.local.dao.DivisionDao
import com.pemmob.schedulyy.data.local.dao.EventDao
import com.pemmob.schedulyy.data.local.dao.ProkerDao
import com.pemmob.schedulyy.data.local.dao.SemesterDao
import com.pemmob.schedulyy.data.local.dao.SpaceDao
import com.pemmob.schedulyy.data.local.dao.SubTaskDao
import com.pemmob.schedulyy.data.local.dao.TaskDao
import com.pemmob.schedulyy.data.local.dao.TaskWithSubTasks
import com.pemmob.schedulyy.data.local.entity.ClassSessionEntity
import com.pemmob.schedulyy.data.local.entity.CourseEntity
import com.pemmob.schedulyy.data.local.entity.DivisionEntity
import com.pemmob.schedulyy.data.local.entity.EventEntity
import com.pemmob.schedulyy.data.local.entity.ProkerEntity
import com.pemmob.schedulyy.data.local.entity.SemesterEntity
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import com.pemmob.schedulyy.data.local.entity.SubTaskEntity
import com.pemmob.schedulyy.data.local.entity.TaskEntity
import com.pemmob.schedulyy.domain.model.TaskStatus
import com.pemmob.schedulyy.domain.repository.ScheduleRepository
import com.pemmob.schedulyy.domain.repository.SpaceRepository
import com.pemmob.schedulyy.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao
) : TaskRepository {

    override fun getAllTasksWithSubTasks(): Flow<List<TaskWithSubTasks>> =
        taskDao.getAllTasksWithSubTasks()

    override fun getTasksBySpace(spaceId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksBySpace(spaceId)

    override suspend fun getTaskById(id: Long): TaskWithSubTasks? =
        taskDao.getTaskWithSubTasks(id)

    override suspend fun insertTask(task: TaskEntity, subTasks: List<SubTaskEntity>): Long {
        val taskId = taskDao.insert(task)
        if (subTasks.isNotEmpty()) {
            val mapped = subTasks.map { it.copy(taskId = taskId) }
            subTaskDao.insertAll(mapped)
        }
        return taskId
    }

    override suspend fun updateTask(task: TaskEntity) {
        taskDao.update(task)
    }

    override suspend fun setTaskStatus(id: Long, status: TaskStatus) {
        val completedAt = if (status == TaskStatus.DONE) Instant.now() else null
        taskDao.updateStatus(id, status, completedAt)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        taskDao.delete(task)
    }

    override suspend fun setSubTaskDone(subTaskId: Long, done: Boolean) {
        subTaskDao.updateDone(subTaskId, done)
    }
}

@Singleton
class ScheduleRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao,
    private val classSessionDao: ClassSessionDao,
    private val semesterDao: SemesterDao
) : ScheduleRepository {

    override fun getAllCoursesWithSessions(): Flow<List<CourseWithSessions>> =
        courseDao.getAllCoursesWithSessions()

    override fun getCoursesBySpace(spaceId: Long): Flow<List<CourseEntity>> =
        courseDao.getCoursesBySpace(spaceId)

    override fun getSessionsByDay(dayOfWeek: Int): Flow<List<ClassSessionEntity>> =
        classSessionDao.getSessionsByDay(dayOfWeek)

    override fun getAllSessions(): Flow<List<ClassSessionEntity>> =
        classSessionDao.getAllSessions()

    override suspend fun getCourseWithSessions(courseId: Long): CourseWithSessions? =
        courseDao.getCourseWithSessions(courseId)

    override suspend fun insertCourse(course: CourseEntity, sessions: List<ClassSessionEntity>): Long {
        val courseId = courseDao.insert(course)
        if (sessions.isNotEmpty()) {
            val mapped = sessions.map { it.copy(courseId = courseId) }
            classSessionDao.insertAll(mapped)
        }
        return courseId
    }

    override suspend fun updateCourse(course: CourseEntity, sessions: List<ClassSessionEntity>) {
        courseDao.update(course)
        classSessionDao.deleteByCourseId(course.id)
        if (sessions.isNotEmpty()) {
            val mapped = sessions.map { it.copy(courseId = course.id) }
            classSessionDao.insertAll(mapped)
        }
    }

    override suspend fun deleteCourse(course: CourseEntity) {
        courseDao.delete(course)
    }

    override suspend fun deleteCourseById(courseId: Long) {
        courseDao.deleteById(courseId)
    }

    override fun getActiveSemester(): Flow<SemesterEntity?> =
        semesterDao.getActiveSemester()

    override fun getAllSemesters(): Flow<List<SemesterEntity>> =
        semesterDao.getAllSemesters()

    override suspend fun saveSemester(semester: SemesterEntity): Long {
        if (semester.active) {
            semesterDao.deactivateAll()
        }
        return if (semester.id == 0L) {
            semesterDao.insert(semester)
        } else {
            semesterDao.update(semester)
            semester.id
        }
    }

    override suspend fun deleteSemester(semester: SemesterEntity) {
        semesterDao.delete(semester)
    }

    override suspend fun deleteSemesterById(id: Long) {
        semesterDao.deleteById(id)
    }
}

@Singleton
class SpaceRepositoryImpl @Inject constructor(
    private val spaceDao: SpaceDao,
    private val divisionDao: DivisionDao,
    private val prokerDao: ProkerDao,
    private val eventDao: EventDao
) : SpaceRepository {

    override fun getAllActiveSpaces(): Flow<List<SpaceEntity>> =
        spaceDao.getAllActiveSpaces()

    override fun getRootSpaces(): Flow<List<SpaceEntity>> =
        spaceDao.getRootSpaces()

    override fun getChildSpaces(parentId: Long): Flow<List<SpaceEntity>> =
        spaceDao.getChildSpaces(parentId)

    override suspend fun getSpaceById(id: Long): SpaceEntity? =
        spaceDao.getSpaceById(id)

    override suspend fun insertSpace(space: SpaceEntity): Long =
        spaceDao.insert(space)

    override suspend fun insertSpaceWithTemplate(space: SpaceEntity, template: String): Long {
        val spaceId = spaceDao.insert(space)
        val defaultDivisions = when (template.uppercase()) {
            "HIMPUNAN" -> listOf(
                "Kesekretariatan" to "sky",
                "Humas" to "mint",
                "Pendidikan" to "butter",
                "Minat Bakat" to "bubblegum",
                "Media Kreatif" to "lilac"
            )
            "KEPANITIAAN" -> listOf(
                "Acara" to "sky",
                "Humas" to "mint",
                "PDD" to "lilac",
                "Konsumsi" to "butter",
                "Perlengkapan" to "bubblegum",
                "Dana Usaha" to "lime",
                "Keamanan" to "sky"
            )
            "UKM" -> listOf(
                "Latihan Rutin" to "mint",
                "Pengurus" to "sky",
                "Event" to "bubblegum"
            )
            else -> emptyList()
        }

        defaultDivisions.forEach { (name, colorKey) ->
            divisionDao.insert(
                DivisionEntity(
                    spaceId = spaceId,
                    name = name,
                    colorKey = colorKey
                )
            )
        }

        return spaceId
    }

    override suspend fun archiveSpace(id: Long, archived: Boolean) {
        spaceDao.setArchived(id, archived)
    }

    override suspend fun deleteSpace(space: SpaceEntity) {
        spaceDao.delete(space)
    }

    // ─── Divisi ───────────────────────────────────────────────────────────────
    override fun getDivisionsBySpace(spaceId: Long): Flow<List<DivisionEntity>> =
        divisionDao.getDivisionsBySpace(spaceId)

    override suspend fun insertDivision(division: DivisionEntity): Long =
        divisionDao.insert(division)

    override suspend fun deleteDivision(division: DivisionEntity) =
        divisionDao.delete(division)

    // ─── Proker ───────────────────────────────────────────────────────────────
    override fun getProkersBySpace(spaceId: Long): Flow<List<ProkerEntity>> =
        prokerDao.getProkersBySpace(spaceId)

    override suspend fun insertProker(proker: ProkerEntity): Long =
        prokerDao.insert(proker)

    override suspend fun updateProker(proker: ProkerEntity) =
        prokerDao.update(proker)

    override suspend fun deleteProker(proker: ProkerEntity) =
        prokerDao.delete(proker)

    // ─── Event ────────────────────────────────────────────────────────────────
    override fun getEventsBySpace(spaceId: Long): Flow<List<EventEntity>> =
        eventDao.getEventsBySpace(spaceId)

    override fun getAllEvents(): Flow<List<EventEntity>> =
        eventDao.getAllEvents()

    override fun getEventsInRange(from: Instant, to: Instant): Flow<List<EventEntity>> =
        eventDao.getEventsInRange(from, to)

    override suspend fun insertEvent(event: EventEntity): Long =
        eventDao.insert(event)

    override suspend fun deleteEvent(event: EventEntity) =
        eventDao.delete(event)
}
