package com.designlife.orchestrator

import android.app.AlarmManager
import android.content.Context
import com.designlife.orchestrator.notification.repository.NotificationStoreRepository
import com.designlife.orchestrator.notification.repository.TaskNotificationRepository
import com.designlife.orchestrator.notification.store.NotificationStore

object NotificationServiceLocator {
    private var taskNotificationRepository : TaskNotificationRepository? = null
    private var notificationStoreRepository : NotificationStoreRepository? = null


    public fun provideNotificationStoreRepository(context: Context) : NotificationStoreRepository{
        return notificationStoreRepository ?: createNotificationStoreRepository(context)
    }

    fun createNotificationStoreRepository(context: Context): NotificationStoreRepository {
        notificationStoreRepository = NotificationStoreRepository(NotificationStore(context))
        return notificationStoreRepository!!
    }

    public fun provideNotificationRepository(context : Context,alarmManager: AlarmManager) : TaskNotificationRepository{
        return taskNotificationRepository ?: createNotificationRepository(context,alarmManager)
    }

    private fun createNotificationRepository(context: Context,alarmManager: AlarmManager): TaskNotificationRepository {
        taskNotificationRepository = TaskNotificationRepository(context,alarmManager,NotificationServiceLocator.provideNotificationStoreRepository(context))
        return taskNotificationRepository!!
    }
}