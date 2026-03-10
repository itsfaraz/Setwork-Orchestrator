package com.designlife.orchestrator.notification.broadcastreceiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.designlife.orchestrator.notification.NotificationServiceLocator
import com.designlife.orchestrator.notification.clickmanager.NotificationClickManager
import com.designlife.orchestrator.data.NotificationInfo
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal class TaskReceiver : BroadcastReceiver(){
    private val scope = MainScope()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED){
            Log.i("FLOW","Reschedule all notifications")
            scope.launch {
                val notifications = NotificationServiceLocator
                    .provideNotificationStoreRepository(context)
                    .readAllNotification()
                if (notifications.isNotEmpty()){
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    NotificationServiceLocator
                        .provideNotificationRepository(context,alarmManager)
                        .scheduleNotification(notifications)
                    Log.i("FLOW","Reschedule all notifications --> Alarm Set")
                }
                Log.i("FLOW","Reschedule all notifications ${notifications}")
            }
        }else{
            val taskService = TaskService(context)
            val title = intent.getStringExtra("title") ?: "New Task ?"
            val subTitle = intent.getStringExtra("subTitle") ?: ""
            val todoId = intent.getIntExtra("todoId",0)
            val classPath = intent.getStringExtra("classPath") ?: ""
            NotificationClickManager.notifyListener(todoId,title,classPath)
//            taskService.showNotification(title,subTitle)
            Log.i("NOTIFICATION_FLOW", "onReceive: received")
            scope.launch {
                NotificationServiceLocator
                    .provideNotificationStoreRepository(context)
                    .deleteNotification(NotificationInfo(taskTitle = title, taskSubTitle = subTitle))
            }
        }
    }
}