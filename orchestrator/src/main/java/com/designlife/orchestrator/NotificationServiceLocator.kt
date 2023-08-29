package com.designlife.orchestrator

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import com.designlife.orchestrator.notification.repository.TaskNotificationRepository

object NotificationServiceLocator {
    private var taskNotificationRepository : TaskNotificationRepository? = null

    public fun provideNotificationRepository(context : Context,alarmManager: AlarmManager) : TaskNotificationRepository{
        return taskNotificationRepository ?: createNotificationRepository(context,alarmManager)
    }

    private fun createNotificationRepository(context: Context,alarmManager: AlarmManager): TaskNotificationRepository {
        taskNotificationRepository = TaskNotificationRepository(context,alarmManager)
        return taskNotificationRepository!!
    }
}