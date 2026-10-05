package com.pemmob.schedulyy.data.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pemmob.schedulyy.data.local.dao.CourseWithSessions
import com.pemmob.schedulyy.data.local.entity.ClassSessionEntity
import com.pemmob.schedulyy.data.local.entity.CourseEntity
import com.pemmob.schedulyy.data.local.entity.TaskEntity
import com.pemmob.schedulyy.domain.model.TaskStatus
import com.pemmob.schedulyy.domain.repository.ScheduleRepository
import com.pemmob.schedulyy.domain.repository.SettingsRepository
import com.pemmob.schedulyy.domain.repository.TaskRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val taskRepository: TaskRepository,
    private val scheduleRepository: ScheduleRepository,
    private val settingsRepository: SettingsRepository
) {
    private val workManager: WorkManager get() = WorkManager.getInstance(context)

    companion object {
        const val TAG_ALL_REMINDERS = "schedulyy_reminders"
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        fun calculateDeadlineTrigger(deadline: Instant, offsetMinutes: Int): Instant {
            return deadline.minus(Duration.ofMinutes(offsetMinutes.toLong()))
        }

        fun calculateH1Trigger(deadline: Instant, zone: ZoneId = ZoneId.systemDefault()): Instant {
            val deadlineDate = deadline.atZone(zone).toLocalDate()
            val h1Date = deadlineDate.minusDays(1)
            return LocalDateTime.of(h1Date, LocalTime.of(19, 0)).atZone(zone).toInstant()
        }

        fun calculateNextSessionTrigger(
            sessionDayOfWeek: Int,
            sessionStartTime: LocalTime,
            now: LocalDateTime = LocalDateTime.now(),
            offsetMinutes: Long = 30
        ): LocalDateTime {
            val day = DayOfWeek.of(sessionDayOfWeek.coerceIn(1, 7))
            var targetDate = now.toLocalDate().with(TemporalAdjusters.nextOrSame(day))
            var triggerDateTime = LocalDateTime.of(targetDate, sessionStartTime).minusMinutes(offsetMinutes)
            if (triggerDateTime.isBefore(now)) {
                targetDate = targetDate.plusWeeks(1)
                triggerDateTime = LocalDateTime.of(targetDate, sessionStartTime).minusMinutes(offsetMinutes)
            }
            return triggerDateTime
        }
    }

    suspend fun scheduleAll() {
        val settings = settingsRepository.getUserSettings().first()
        if (!settings.notificationsEnabled) {
            cancelAll()
            return
        }

        val defaultOffsetMinutes = settings.defaultReminderMinutes

        // 1. Jadwalkan Pengingat Tugas
        val tasksWithSubTasks = taskRepository.getAllTasksWithSubTasks().first()
        val activeTasks = tasksWithSubTasks.map { it.task }.filter { it.status != TaskStatus.DONE }
        for (task in activeTasks) {
            scheduleTaskReminder(task, defaultOffsetMinutes)
        }

        // 2. Jadwalkan Pengingat Kelas Kuliah (30 menit sebelum mulai)
        val coursesWithSessions = scheduleRepository.getAllCoursesWithSessions().first()
        for (courseWithSessions in coursesWithSessions) {
            for (session in courseWithSessions.sessions) {
                scheduleSessionReminder(courseWithSessions.course, session)
            }
        }
    }

    fun scheduleTaskReminder(task: TaskEntity, offsetMinutes: Int = 60) {
        val deadline = task.deadline ?: return
        if (task.status == TaskStatus.DONE) {
            cancelTaskReminder(task.id)
            return
        }

        val now = Instant.now()
        val zone = ZoneId.systemDefault()

        // ── A. Pengingat Sesaat Sebelum Deadline (jika ada jam spesifik) ───
        if (task.hasTime) {
            val triggerInstant = calculateDeadlineTrigger(deadline, offsetMinutes)
            if (triggerInstant.isAfter(now)) {
                val delayMs = Duration.between(now, triggerInstant).toMillis()
                val deadlineTimeStr = deadline.atZone(zone).toLocalTime().format(TIME_FORMATTER)

                val data = Data.Builder()
                    .putString(ReminderWorker.KEY_TYPE, ReminderWorker.TYPE_TASK)
                    .putInt(ReminderWorker.KEY_ID, task.id.toInt())
                    .putString(ReminderWorker.KEY_TITLE, "Tenggat Tugas: ${task.title}")
                    .putString(ReminderWorker.KEY_MESSAGE, "Batas waktu pukul $deadlineTimeStr ($offsetMinutes menit lagi).")
                    .build()

                val work = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag(TAG_ALL_REMINDERS)
                    .addTag("task_${task.id}")
                    .build()

                workManager.enqueueUniqueWork(
                    "task_deadline_${task.id}",
                    ExistingWorkPolicy.REPLACE,
                    work
                )
            }
        }

        // ── B. Pengingat H-1 Pukul 19:00 Malam Sebelumnya ─────────────────
        val h1Trigger = calculateH1Trigger(deadline, zone)
        if (h1Trigger.isAfter(now) && h1Trigger.isBefore(deadline)) {
            val delayMs = Duration.between(now, h1Trigger).toMillis()

            val data = Data.Builder()
                .putString(ReminderWorker.KEY_TYPE, ReminderWorker.TYPE_TASK)
                .putInt(ReminderWorker.KEY_ID, (task.id + 100000).toInt())
                .putString(ReminderWorker.KEY_TITLE, "Pengingat Besok: ${task.title}")
                .putString(ReminderWorker.KEY_MESSAGE, "Tugas ini memiliki tenggat besok. Selesaikan malam ini agar lebih tenang!")
                .build()

            val work = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag(TAG_ALL_REMINDERS)
                .addTag("task_${task.id}")
                .build()

            workManager.enqueueUniqueWork(
                "task_h1_${task.id}",
                ExistingWorkPolicy.REPLACE,
                work
            )
        }
    }

    fun cancelTaskReminder(taskId: Long) {
        workManager.cancelUniqueWork("task_deadline_$taskId")
        workManager.cancelUniqueWork("task_h1_$taskId")
    }

    fun scheduleSessionReminder(course: CourseEntity, session: ClassSessionEntity) {
        val now = LocalDateTime.now()
        val triggerDateTime = calculateNextSessionTrigger(session.dayOfWeek, session.startTime, now, 30)
        val delayMs = Duration.between(now, triggerDateTime).toMillis()
        val roomInfo = session.room?.takeIf { it.isNotBlank() }?.let { "di $it" } ?: ""
        val timeStr = session.startTime.format(TIME_FORMATTER)

        val data = Data.Builder()
            .putString(ReminderWorker.KEY_TYPE, ReminderWorker.TYPE_SCHEDULE)
            .putInt(ReminderWorker.KEY_ID, session.id.toInt())
            .putString(ReminderWorker.KEY_TITLE, course.name)
            .putString(ReminderWorker.KEY_MESSAGE, "Mulai pukul $timeStr $roomInfo (30 menit lagi).")
            .build()

        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(TAG_ALL_REMINDERS)
            .addTag("session_${session.id}")
            .build()

        workManager.enqueueUniqueWork(
            "session_reminder_${session.id}",
            ExistingWorkPolicy.REPLACE,
            work
        )
    }

    fun cancelSessionReminder(sessionId: Long) {
        workManager.cancelUniqueWork("session_reminder_$sessionId")
    }

    fun cancelAll() {
        workManager.cancelAllWorkByTag(TAG_ALL_REMINDERS)
    }
}
