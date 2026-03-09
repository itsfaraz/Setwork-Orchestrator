package com.designlife.orchestrator.notification.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.designlife.orchestrator.notification.broadcastreceiver.TaskReceiver
import com.designlife.orchestrator.notification.data.NotificationInfo

class TaskNotificationRepository(
    private val context: Context,
    private val alarmManager: AlarmManager,
    private val notificationStoreRepository: NotificationStoreRepository
) {
    companion object{
//        var classPath = "com.designlife.justdo.MainActivity"
        var classPath = "com.designlife.justdo_orchestrator.MainActivity"
    }
    suspend fun scheduleNotification(notificationInfoList : List<NotificationInfo>){
        val pendingIntents = mutableListOf<PendingIntent>()
        notificationInfoList.forEachIndexed{ index, notificationInfo ->
            val intent = Intent(context, TaskReceiver::class.java)
            intent.putExtra("title",notificationInfo.taskTitle)
            intent.putExtra("subTitle",notificationInfo.taskSubTitle)
            intent.putExtra("todoId",notificationInfo.taskId)
            intent.putExtra("classPath", classPath)
            pendingIntents.add(
                PendingIntent.getBroadcast(
                    context,
                    (System.currentTimeMillis() + notificationInfo.time.hashCode()).toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            if (notificationInfo.time >= System.currentTimeMillis()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notificationInfo.time,
                    pendingIntents.get(index)
                )
            }
            notificationStoreRepository.storeNotifications(notificationInfoList)
        }
    }
}