package com.pemmob.schedulyy.data.backup

import androidx.room.withTransaction
import com.pemmob.schedulyy.data.local.SchedulyyDatabase
import com.pemmob.schedulyy.data.local.entity.ClassSessionEntity
import com.pemmob.schedulyy.data.local.entity.CourseEntity
import com.pemmob.schedulyy.data.local.entity.DivisionEntity
import com.pemmob.schedulyy.data.local.entity.EventEntity
import com.pemmob.schedulyy.data.local.entity.ProkerEntity
import com.pemmob.schedulyy.data.local.entity.SemesterEntity
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import com.pemmob.schedulyy.data.local.entity.SubTaskEntity
import com.pemmob.schedulyy.data.local.entity.TaskEntity
import com.pemmob.schedulyy.domain.model.EventType
import com.pemmob.schedulyy.domain.model.ProkerStatus
import com.pemmob.schedulyy.domain.model.SpaceType
import com.pemmob.schedulyy.domain.model.TaskPriority
import com.pemmob.schedulyy.domain.model.TaskSource
import com.pemmob.schedulyy.domain.model.TaskStatus
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataBackupManager @Inject constructor(
    private val database: SchedulyyDatabase
) {

    suspend fun exportToJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", Instant.now().toString())

        // 1. Spaces
        val spaces = database.spaceDao().getAllSpacesSync()
        val spacesArray = JSONArray()
        for (s in spaces) {
            val obj = JSONObject().apply {
                put("id", s.id)
                put("parentId", s.parentId ?: JSONObject.NULL)
                put("type", s.type.name)
                put("name", s.name)
                put("colorKey", s.colorKey)
                put("iconKey", s.iconKey)
                put("archived", s.archived)
                put("createdAt", s.createdAt.toString())
            }
            spacesArray.put(obj)
        }
        root.put("spaces", spacesArray)

        // 2. Semesters
        val semesters = database.semesterDao().getAllSemestersSync()
        val semestersArray = JSONArray()
        for (sm in semesters) {
            val obj = JSONObject().apply {
                put("id", sm.id)
                put("name", sm.name)
                put("startDate", sm.startDate.toString())
                put("endDate", sm.endDate.toString())
                put("active", sm.active)
            }
            semestersArray.put(obj)
        }
        root.put("semesters", semestersArray)

        // 3. Courses
        val courses = database.courseDao().getAllCoursesSync()
        val coursesArray = JSONArray()
        for (c in courses) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("spaceId", c.spaceId)
                put("semesterId", c.semesterId ?: JSONObject.NULL)
                put("name", c.name)
                put("code", c.code ?: JSONObject.NULL)
                put("lecturer", c.lecturer ?: JSONObject.NULL)
                put("room", c.room ?: JSONObject.NULL)
                put("sks", c.sks ?: JSONObject.NULL)
                put("colorKey", c.colorKey)
                put("iconKey", c.iconKey)
                put("link", c.link ?: JSONObject.NULL)
            }
            coursesArray.put(obj)
        }
        root.put("courses", coursesArray)

        // 4. Class Sessions
        val sessions = database.classSessionDao().getAllSessionsSync()
        val sessionsArray = JSONArray()
        for (cs in sessions) {
            val obj = JSONObject().apply {
                put("id", cs.id)
                put("courseId", cs.courseId)
                put("dayOfWeek", cs.dayOfWeek)
                put("startTime", cs.startTime.toString())
                put("endTime", cs.endTime.toString())
                put("room", cs.room ?: JSONObject.NULL)
            }
            sessionsArray.put(obj)
        }
        root.put("sessions", sessionsArray)

        // 5. Divisions
        val divisions = database.divisionDao().getAllDivisionsSync()
        val divisionsArray = JSONArray()
        for (d in divisions) {
            val obj = JSONObject().apply {
                put("id", d.id)
                put("spaceId", d.spaceId)
                put("name", d.name)
                put("colorKey", d.colorKey ?: JSONObject.NULL)
            }
            divisionsArray.put(obj)
        }
        root.put("divisions", divisionsArray)

        // 6. Prokers
        val prokers = database.prokerDao().getAllProkersSync()
        val prokersArray = JSONArray()
        for (p in prokers) {
            val obj = JSONObject().apply {
                put("id", p.id)
                put("spaceId", p.spaceId)
                put("divisionId", p.divisionId ?: JSONObject.NULL)
                put("name", p.name)
                put("description", p.description ?: JSONObject.NULL)
                put("startDate", p.startDate?.toString() ?: JSONObject.NULL)
                put("endDate", p.endDate?.toString() ?: JSONObject.NULL)
                put("status", p.status.name)
            }
            prokersArray.put(obj)
        }
        root.put("prokers", prokersArray)

        // 7. Events
        val events = database.eventDao().getAllEventsSync()
        val eventsArray = JSONArray()
        for (e in events) {
            val obj = JSONObject().apply {
                put("id", e.id)
                put("spaceId", e.spaceId)
                put("prokerId", e.prokerId ?: JSONObject.NULL)
                put("title", e.title)
                put("start", e.start.toString())
                put("end", e.end?.toString() ?: JSONObject.NULL)
                put("location", e.location ?: JSONObject.NULL)
                put("type", e.type.name)
                put("notes", e.notes ?: JSONObject.NULL)
                put("repeatWeekly", e.repeatWeekly)
            }
            eventsArray.put(obj)
        }
        root.put("events", eventsArray)

        // 8. Tasks
        val tasks = database.taskDao().getAllTasksSync()
        val tasksArray = JSONArray()
        for (t in tasks) {
            val obj = JSONObject().apply {
                put("id", t.id)
                put("spaceId", t.spaceId)
                put("courseId", t.courseId ?: JSONObject.NULL)
                put("prokerId", t.prokerId ?: JSONObject.NULL)
                put("title", t.title)
                put("notes", t.notes ?: JSONObject.NULL)
                put("deadline", t.deadline?.toString() ?: JSONObject.NULL)
                put("hasTime", t.hasTime)
                put("priority", t.priority.name)
                put("status", t.status.name)
                put("source", t.source.name)
                put("assigneeLabel", t.assigneeLabel ?: JSONObject.NULL)
                put("createdAt", t.createdAt.toString())
                put("completedAt", t.completedAt?.toString() ?: JSONObject.NULL)
            }
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        // 9. SubTasks
        val subTasks = database.subTaskDao().getAllSubTasksSync()
        val subTasksArray = JSONArray()
        for (st in subTasks) {
            val obj = JSONObject().apply {
                put("id", st.id)
                put("taskId", st.taskId)
                put("title", st.title)
                put("done", st.done)
                put("position", st.position)
            }
            subTasksArray.put(obj)
        }
        root.put("subTasks", subTasksArray)

        return root.toString(2)
    }

    suspend fun restoreFromJson(jsonString: String): Result<Int> = runCatching {
        val root = JSONObject(jsonString)
        var totalRestored = 0

        database.withTransaction {
            // Restore Spaces
            if (root.has("spaces")) {
                val array = root.getJSONArray("spaces")
                val list = mutableListOf<SpaceEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        SpaceEntity(
                            id = obj.optLong("id", 0L),
                            parentId = if (obj.isNull("parentId")) null else obj.optLong("parentId"),
                            type = SpaceType.valueOf(obj.optString("type", SpaceType.PRIBADI.name)),
                            name = obj.optString("name", "Space"),
                            colorKey = obj.optString("colorKey", "pastelButter"),
                            iconKey = obj.optString("iconKey", "folder"),
                            archived = obj.optBoolean("archived", false),
                            createdAt = obj.optString("createdAt").takeIf { it.isNotBlank() }?.let { Instant.parse(it) } ?: Instant.now()
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    database.spaceDao().insertAll(list)
                    totalRestored += list.size
                }
            }

            // Restore Semesters
            if (root.has("semesters")) {
                val array = root.getJSONArray("semesters")
                val list = mutableListOf<SemesterEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        SemesterEntity(
                            id = obj.optLong("id", 0L),
                            name = obj.optString("name", "Semester"),
                            startDate = LocalDate.parse(obj.getString("startDate")),
                            endDate = LocalDate.parse(obj.getString("endDate")),
                            active = obj.optBoolean("active", false)
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    database.semesterDao().insertAll(list)
                    totalRestored += list.size
                }
            }

            // Restore Courses
            if (root.has("courses")) {
                val array = root.getJSONArray("courses")
                val list = mutableListOf<CourseEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        CourseEntity(
                            id = obj.optLong("id", 0L),
                            spaceId = obj.optLong("spaceId", 1L),
                            semesterId = if (obj.isNull("semesterId")) null else obj.optLong("semesterId"),
                            name = obj.optString("name", "Matakuliah"),
                            code = if (obj.isNull("code")) null else obj.optString("code"),
                            lecturer = if (obj.isNull("lecturer")) null else obj.optString("lecturer"),
                            room = if (obj.isNull("room")) null else obj.optString("room"),
                            sks = if (obj.isNull("sks")) null else obj.optInt("sks"),
                            colorKey = obj.optString("colorKey", "sky"),
                            iconKey = obj.optString("iconKey", "school"),
                            link = if (obj.isNull("link")) null else obj.optString("link")
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    database.courseDao().insertAll(list)
                    totalRestored += list.size
                }
            }

            // Restore Class Sessions
            if (root.has("sessions")) {
                val array = root.getJSONArray("sessions")
                val list = mutableListOf<ClassSessionEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        ClassSessionEntity(
                            id = obj.optLong("id", 0L),
                            courseId = obj.optLong("courseId", 0L),
                            dayOfWeek = obj.optInt("dayOfWeek", 1),
                            startTime = LocalTime.parse(obj.getString("startTime")),
                            endTime = LocalTime.parse(obj.getString("endTime")),
                            room = if (obj.isNull("room")) null else obj.optString("room")
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    database.classSessionDao().insertAll(list)
                    totalRestored += list.size
                }
            }

            // Restore Divisions
            if (root.has("divisions")) {
                val array = root.getJSONArray("divisions")
                val list = mutableListOf<DivisionEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        DivisionEntity(
                            id = obj.optLong("id", 0L),
                            spaceId = obj.optLong("spaceId", 1L),
                            name = obj.optString("name", "Divisi"),
                            colorKey = if (obj.isNull("colorKey")) null else obj.optString("colorKey")
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    database.divisionDao().insertAll(list)
                    totalRestored += list.size
                }
            }

            // Restore Prokers
            if (root.has("prokers")) {
                val array = root.getJSONArray("prokers")
                val list = mutableListOf<ProkerEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        ProkerEntity(
                            id = obj.optLong("id", 0L),
                            spaceId = obj.optLong("spaceId", 1L),
                            divisionId = if (obj.isNull("divisionId")) null else obj.optLong("divisionId"),
                            name = obj.optString("name", "Proker"),
                            description = if (obj.isNull("description")) null else obj.optString("description"),
                            startDate = if (obj.isNull("startDate")) null else LocalDate.parse(obj.getString("startDate")),
                            endDate = if (obj.isNull("endDate")) null else LocalDate.parse(obj.getString("endDate")),
                            status = ProkerStatus.valueOf(obj.optString("status", ProkerStatus.RENCANA.name))
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    database.prokerDao().insertAll(list)
                    totalRestored += list.size
                }
            }

            // Restore Events
            if (root.has("events")) {
                val array = root.getJSONArray("events")
                val list = mutableListOf<EventEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        EventEntity(
                            id = obj.optLong("id", 0L),
                            spaceId = obj.optLong("spaceId", 1L),
                            prokerId = if (obj.isNull("prokerId")) null else obj.optLong("prokerId"),
                            title = obj.optString("title", "Event"),
                            start = Instant.parse(obj.getString("start")),
                            end = if (obj.isNull("end")) null else Instant.parse(obj.getString("end")),
                            location = if (obj.isNull("location")) null else obj.optString("location"),
                            type = EventType.valueOf(obj.optString("type", EventType.RAPAT.name)),
                            notes = if (obj.isNull("notes")) null else obj.optString("notes"),
                            repeatWeekly = obj.optBoolean("repeatWeekly", false)
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    database.eventDao().insertAll(list)
                    totalRestored += list.size
                }
            }

            // Restore Tasks
            if (root.has("tasks")) {
                val array = root.getJSONArray("tasks")
                val list = mutableListOf<TaskEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        TaskEntity(
                            id = obj.optLong("id", 0L),
                            spaceId = obj.optLong("spaceId", 1L),
                            courseId = if (obj.isNull("courseId")) null else obj.optLong("courseId"),
                            prokerId = if (obj.isNull("prokerId")) null else obj.optLong("prokerId"),
                            title = obj.optString("title", "Tugas"),
                            notes = if (obj.isNull("notes")) null else obj.optString("notes"),
                            deadline = if (obj.isNull("deadline")) null else Instant.parse(obj.getString("deadline")),
                            hasTime = obj.optBoolean("hasTime", false),
                            priority = TaskPriority.valueOf(obj.optString("priority", TaskPriority.MEDIUM.name)),
                            status = TaskStatus.valueOf(obj.optString("status", TaskStatus.TODO.name)),
                            source = TaskSource.valueOf(obj.optString("source", TaskSource.MANUAL.name)),
                            assigneeLabel = if (obj.isNull("assigneeLabel")) null else obj.optString("assigneeLabel"),
                            createdAt = Instant.parse(obj.getString("createdAt")),
                            completedAt = if (obj.isNull("completedAt")) null else Instant.parse(obj.getString("completedAt"))
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    database.taskDao().insertAll(list)
                    totalRestored += list.size
                }
            }

            // Restore SubTasks
            if (root.has("subTasks")) {
                val array = root.getJSONArray("subTasks")
                val list = mutableListOf<SubTaskEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        SubTaskEntity(
                            id = obj.optLong("id", 0L),
                            taskId = obj.optLong("taskId", 0L),
                            title = obj.optString("title", "Subtugas"),
                            done = obj.optBoolean("done", false),
                            position = obj.optInt("position", 0)
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    database.subTaskDao().insertAll(list)
                    totalRestored += list.size
                }
            }
        }

        totalRestored
    }
}
