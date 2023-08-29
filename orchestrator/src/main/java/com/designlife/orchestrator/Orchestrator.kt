package com.designlife.orchestrator

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.designlife.orchestrator.notification.broadcastreceiver.TaskService

class Orchestrator : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.i("FLOW", "onCreate: Channel Created")
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                TaskService.TODO_WIZ_CHANNEL,
                "todowiz_events",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "For TodoWiz events"
            notificationManager.createNotificationChannel(channel)
        }
    }
}