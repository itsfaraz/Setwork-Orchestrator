package com.designlife.orchestrator

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import com.designlife.orchestrator.notification.NotificationServiceLocator
import com.designlife.orchestrator.notification.broadcastreceiver.NotificationBroadcastReceiver
import com.designlife.orchestrator.notification.broadcastreceiver.TaskService
import com.designlife.orchestrator.notification.worker.NotificationCleanupWorker
import java.util.concurrent.TimeUnit

internal class Orchestrator : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        NotificationServiceLocator.provideAppStoreRepository(this)
        scheduleNotificationCleanup()
        Log.i("FLOW", "onCreate: Channel Created")
    }

    private fun createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManager = getSystemService(NotificationManager::class.java)
            val taskChannel = NotificationChannel(
                NotificationBroadcastReceiver.SETWORK_TASK_CHANNEL,
                "setwork_tasks",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            taskChannel.description = "Setwork Task's Notification Channel"

            val noteChannel = NotificationChannel(
                NotificationBroadcastReceiver.SETWORK_NOTE_CHANNEL,
                "setwork_notes",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            noteChannel.description =  "Setwork Note's Notification Channel"

            val deckChannel = NotificationChannel(
                NotificationBroadcastReceiver.SETWORK_DECK_CHANNEL,
                "setwork_deck",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            deckChannel.description =  "Setwork Deck's Notification Channel"

            val appUpdateChannel = NotificationChannel(
                NotificationBroadcastReceiver.SETWORK_DELIVERY_CHANNEL,
                "setwork_delivery",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            appUpdateChannel.description =  "Setwork Delivery Notification Channel"

            notificationManager.createNotificationChannel(taskChannel)
            notificationManager.createNotificationChannel(noteChannel)
            notificationManager.createNotificationChannel(deckChannel)
            notificationManager.createNotificationChannel(appUpdateChannel)
        }
    }

    private fun scheduleNotificationCleanup() {
        val cleanupWork = PeriodicWorkRequestBuilder<NotificationCleanupWorker>(
            1,
            TimeUnit.DAYS
        )
            .addTag("notification_cleanup")
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        NotificationServiceLocator.provideWorkManager(this).enqueueUniquePeriodicWork(
            "notification_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupWork
        )
    }
}