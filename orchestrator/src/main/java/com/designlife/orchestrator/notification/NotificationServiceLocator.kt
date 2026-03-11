package com.designlife.orchestrator.notification

import android.app.AlarmManager
import android.content.Context
import androidx.work.WorkManager
import com.designlife.orchestrator.NotificationSchedulerImpl
import com.designlife.orchestrator.data.room.AppDatabase
import com.designlife.orchestrator.domain.repository.AppStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal object NotificationServiceLocator {
    private var alarmManager : AlarmManager? = null
    private var workManager : WorkManager? = null

    private var appStoreRepository : AppStoreRepository? = null

    private var coroutineScope : CoroutineScope? = null

    private var notificationScheduler : NotificationSchedulerImpl? = null


    internal fun provideAlarmManager(context : Context) : AlarmManager{
        return alarmManager ?: createAlarmManager(context)
    }

    private fun createAlarmManager(context: Context): AlarmManager {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager!!
    }

    internal fun provideWorkManager(context : Context) : WorkManager{
        return workManager ?: createWorkManager(context)
    }

    private fun createWorkManager(context: Context): WorkManager {
        workManager = WorkManager.getInstance(
            context
        )
        return workManager!!
    }

    internal fun provideAppStoreRepository(context: Context) : AppStoreRepository{
        return appStoreRepository ?: createAppStoreRepository(context)
    }

    private fun createAppStoreRepository(context: Context) : AppStoreRepository{
        val dao = AppDatabase.getDatabase(context).notificationDao()
        appStoreRepository = AppStoreRepository(dao)
        return appStoreRepository!!
    }

    internal fun provideCoroutineScope() : CoroutineScope{
        return coroutineScope ?: createCoroutineScope()
    }

    private fun createCoroutineScope(): CoroutineScope {
        coroutineScope = CoroutineScope(Dispatchers.IO)
        return coroutineScope!!
    }

    internal fun provideNotificationScheduler(context: Context) : NotificationSchedulerImpl{
        return notificationScheduler ?: createNotificationScheduler(context)
    }

    private fun createNotificationScheduler(context: Context) : NotificationSchedulerImpl{
        notificationScheduler = NotificationSchedulerImpl(context)
        return notificationScheduler!!
    }

}