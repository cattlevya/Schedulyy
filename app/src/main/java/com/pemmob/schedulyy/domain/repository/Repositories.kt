package com.pemmob.schedulyy.domain.repository

import com.pemmob.schedulyy.data.local.dao.CourseWithSessions
import com.pemmob.schedulyy.data.local.dao.TaskWithSubTasks
import com.pemmob.schedulyy.data.local.entity.ClassSessionEntity
import com.pemmob.schedulyy.data.local.entity.CourseEntity
import com.pemmob.schedulyy.data.local.entity.DivisionEntity
import com.pemmob.schedulyy.data.local.entity.EventEntity
import com.pemmob.schedulyy.data.local.entity.ProkerEntity
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import com.pemmob.schedulyy.data.local.entity.SubTaskEntity
import com.pemmob.schedulyy.data.local.entity.TaskEntity
import com.pemmob.schedulyy.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface TaskRepository {
    fun getAllTasksWithSubTasks(): Flow<List<TaskWithSubTasks>>
    fun getTasksBySpace(spaceId: Long): Flow<List<TaskEntity>>
    suspend fun getTaskById(id: Long): TaskWithSubTasks?
    suspend fun insertTask(task: TaskEntity, subTasks: List<SubTaskEntity> = emptyList()): Long
    suspend fun updateTask(task: TaskEntity)
    suspend fun setTaskStatus(id: Long, status: TaskStatus)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun setSubTaskDone(subTaskId: Long, done: Boolean)
}

interface ScheduleRepository {
    fun getAllCoursesWithSessions(): Flow<List<CourseWithSessions>>
    fun getCoursesBySpace(spaceId: Long): Flow<List<CourseEntity>>
    fun getSessionsByDay(dayOfWeek: Int): Flow<List<ClassSessionEntity>>
    fun getAllSessions(): Flow<List<ClassSessionEntity>>
    suspend fun getCourseWithSessions(courseId: Long): CourseWithSessions?
    suspend fun insertCourse(course: CourseEntity, sessions: List<ClassSessionEntity>): Long
    suspend fun updateCourse(course: CourseEntity, sessions: List<ClassSessionEntity>)
    suspend fun deleteCourse(course: CourseEntity)
    suspend fun deleteCourseById(courseId: Long)

    fun getActiveSemester(): Flow<com.pemmob.schedulyy.data.local.entity.SemesterEntity?>
    fun getAllSemesters(): Flow<List<com.pemmob.schedulyy.data.local.entity.SemesterEntity>>
    suspend fun saveSemester(semester: com.pemmob.schedulyy.data.local.entity.SemesterEntity): Long
    suspend fun deleteSemester(semester: com.pemmob.schedulyy.data.local.entity.SemesterEntity)
    suspend fun deleteSemesterById(id: Long)
}

interface SpaceRepository {
    fun getAllActiveSpaces(): Flow<List<SpaceEntity>>
    fun getRootSpaces(): Flow<List<SpaceEntity>>
    fun getChildSpaces(parentId: Long): Flow<List<SpaceEntity>>
    suspend fun getSpaceById(id: Long): SpaceEntity?
    suspend fun insertSpace(space: SpaceEntity): Long
    suspend fun insertSpaceWithTemplate(space: SpaceEntity, template: String): Long
    suspend fun archiveSpace(id: Long, archived: Boolean)
    suspend fun deleteSpace(space: SpaceEntity)

    // Divisi
    fun getDivisionsBySpace(spaceId: Long): Flow<List<DivisionEntity>>
    suspend fun insertDivision(division: DivisionEntity): Long
    suspend fun deleteDivision(division: DivisionEntity)

    // Proker
    fun getProkersBySpace(spaceId: Long): Flow<List<ProkerEntity>>
    suspend fun insertProker(proker: ProkerEntity): Long
    suspend fun updateProker(proker: ProkerEntity)
    suspend fun deleteProker(proker: ProkerEntity)

    // Event
    fun getEventsBySpace(spaceId: Long): Flow<List<EventEntity>>
    fun getAllEvents(): Flow<List<EventEntity>>
    fun getEventsInRange(from: Instant, to: Instant): Flow<List<EventEntity>>
    suspend fun insertEvent(event: EventEntity): Long
    suspend fun deleteEvent(event: EventEntity)
}

data class UserSettings(
    val hasApiKey: Boolean = false,
    val maskedApiKey: String = "",
    val selectedModel: String = "gemini-1.5-flash",
    val defaultReminderMinutes: Int = 60,
    val notificationsEnabled: Boolean = true,
    val studentName: String = "Dimas Pratama",
    val nim: String = "2310101049",
    val major: String = "Informatika",
    val university: String = "Universitas Nusantara",
    val semesterName: String = "5",
    val cardColorKey: String = "pastelButter",
    val hasCompletedOnboarding: Boolean = false
)

interface SettingsRepository {
    fun getUserSettings(): Flow<UserSettings>
    suspend fun getDecryptedApiKey(): String
    suspend fun saveApiKey(apiKey: String)
    suspend fun clearApiKey()
    suspend fun setSelectedModel(model: String)
    suspend fun setDefaultReminderMinutes(minutes: Int)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun updateProfile(
        name: String,
        nim: String,
        major: String,
        university: String,
        semester: String,
        cardColorKey: String
    )
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun clearAllData()
}
