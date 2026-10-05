package com.pemmob.schedulyy.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pemmob.schedulyy.MainActivity
import com.pemmob.schedulyy.R

object NotificationHelper {

    const val CHANNEL_TASKS = "channel_tasks"
    const val CHANNEL_SCHEDULE = "channel_schedule"
    const val CHANNEL_EVENTS = "channel_events"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Channel Deadline Tugas
            val tasksChannel = NotificationChannel(
                CHANNEL_TASKS,
                "Deadline Tugas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pengingat batas waktu tugas kuliah dan organisasi"
                enableVibration(true)
                setShowBadge(true)
            }

            // 2. Channel Jadwal Kuliah
            val scheduleChannel = NotificationChannel(
                CHANNEL_SCHEDULE,
                "Jadwal Kuliah",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pengingat sebelum sesi kelas kuliah dimulai"
                enableVibration(true)
                setShowBadge(true)
            }

            // 3. Channel Kegiatan Organisasi
            val eventsChannel = NotificationChannel(
                CHANNEL_EVENTS,
                "Kegiatan Organisasi",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pengingat rapat dan agenda kegiatan"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannels(listOf(tasksChannel, scheduleChannel, eventsChannel))
        }
    }

    private fun createContentIntent(context: Context, targetScreen: String? = null): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (targetScreen != null) {
                putExtra("target_screen", targetScreen)
            }
        }
        return PendingIntent.getActivity(
            context,
            (System.currentTimeMillis() % 10000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun showTaskNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_TASKS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(createContentIntent(context, "tasks"))

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Android 13+ bila izin belum diberikan user
        }
    }

    fun showScheduleNotification(
        context: Context,
        notificationId: Int,
        courseName: String,
        roomAndTime: String
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_SCHEDULE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Kelas Segera Mulai: $courseName")
            .setContentText(roomAndTime)
            .setStyle(NotificationCompat.BigTextStyle().bigText(roomAndTime))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(createContentIntent(context, "schedule"))

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Izin belum diberikan
        }
    }

    fun showEventNotification(
        context: Context,
        notificationId: Int,
        eventTitle: String,
        message: String
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_EVENTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Agenda Kegiatan: $eventTitle")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(createContentIntent(context, "spaces"))

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Izin belum diberikan
        }
    }
}
