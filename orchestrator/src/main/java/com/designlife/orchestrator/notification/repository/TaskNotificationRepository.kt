package com.designlife.orchestrator.notification.repository

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.designlife.orchestrator.notification.broadcastreceiver.TaskReceiver
import com.designlife.orchestrator.notification.broadcastreceiver.TaskService
import java.util.Date

class TaskNotificationRepository(
    private val context: Context,
    private val alarmManager: AlarmManager
) {
    companion object{
        var classPath = "com.designlife.justdo_orchestrator.MainActivity"// "com.designlife.justdo.MainActivity"
    }
    fun scheduleNotification(notificationInfo : List<Triple<Date,String,Int>>){
        val pendingIntents = mutableListOf<PendingIntent>()
        for (p in notificationInfo.indices){
            val intent = Intent(context, TaskReceiver::class.java)
            intent.putExtra("title",notificationInfo.get(p).second)
            intent.putExtra("todoId",notificationInfo.get(p).third)
            intent.putExtra("classPath", classPath)
            pendingIntents.add(
                PendingIntent.getBroadcast(
                    context,
                    (System.currentTimeMillis() + notificationInfo.get(p).second.hashCode()).toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                notificationInfo.get(p).first.time,
                pendingIntents.get(p)
            )
        }
    }
}