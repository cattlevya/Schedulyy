package com.pemmob.schedulyy

import android.app.Application
import com.pemmob.schedulyy.data.notification.NotificationHelper
import com.pemmob.schedulyy.data.notification.ReminderScheduler
import com.pemmob.schedulyy.di.SpaceSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SchedulyyApp : Application() {

    @Inject
    lateinit var spaceSeeder: SpaceSeeder

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onCreate() {
        super.onCreate()
        // Buat notification channels terpisah (Fase 7)
        NotificationHelper.createNotificationChannels(this)

        // Jadwalkan pengingat di background
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                reminderScheduler.scheduleAll()
            } catch (e: Exception) {
                // Ignore initial schedule failure
            }
        }
    }
}
