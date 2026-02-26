package com.designlife.orchestrator.notification.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.designlife.orchestrator.notification.clickmanager.NotificationClickManager

class TaskReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val taskService = TaskService(context)
        val title = intent.getStringExtra("title") ?: "New Task ?"
        val subTitle = intent.getStringExtra("subTitle") ?: ""
        val todoId = intent.getIntExtra("todoId",0)
        val classPath = intent.getStringExtra("classPath") ?: ""
        NotificationClickManager.notifyListener(todoId,title,classPath)
        taskService.showNotification(title,subTitle)
        Log.i("NOTIFICATION_FLOW", "onReceive: received")
    }

}