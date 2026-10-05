package com.pemmob.schedulyy.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_TYPE = "key_type"
        const val KEY_ID = "key_id"
        const val KEY_TITLE = "key_title"
        const val KEY_MESSAGE = "key_message"

        const val TYPE_TASK = "TASK"
        const val TYPE_SCHEDULE = "SCHEDULE"
        const val TYPE_EVENT = "EVENT"
    }

    override suspend fun doWork(): Result {
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()
        val id = inputData.getInt(KEY_ID, (System.currentTimeMillis() % 10000).toInt())
        val title = inputData.getString(KEY_TITLE) ?: "Pengingat"
        val message = inputData.getString(KEY_MESSAGE) ?: ""

        when (type) {
            TYPE_TASK -> {
                NotificationHelper.showTaskNotification(
                    context = context,
                    notificationId = id,
                    title = title,
                    message = message
                )
            }
            TYPE_SCHEDULE -> {
                NotificationHelper.showScheduleNotification(
                    context = context,
                    notificationId = id,
                    courseName = title,
                    roomAndTime = message
                )
            }
            TYPE_EVENT -> {
                NotificationHelper.showEventNotification(
                    context = context,
                    notificationId = id,
                    eventTitle = title,
                    message = message
                )
            }
        }

        return Result.success()
    }
}
