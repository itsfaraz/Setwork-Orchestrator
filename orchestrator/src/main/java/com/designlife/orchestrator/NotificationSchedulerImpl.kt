package com.designlife.orchestrator

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationPriority
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType
import com.designlife.orchestrator.data.NotificationTypeI
import com.designlife.orchestrator.notification.NotificationServiceLocator
import com.designlife.orchestrator.notification.broadcastreceiver.NotificationBroadcastReceiver
import com.designlife.orchestrator.notification.worker.NotificationWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

internal class NotificationSchedulerImpl(
    private val context: Context
) : NotificationScheduler {

    private val tag : String = "FLOW"
    private val appStoreRepository = NotificationServiceLocator.provideAppStoreRepository(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val workManager = NotificationServiceLocator.provideWorkManager(context)
    private val scope = NotificationServiceLocator.provideCoroutineScope()

    override fun scheduleNotification(notificationInfo: NotificationInfo) {
        scope.launch {
            when(notificationInfo.notificationType){
                NotificationType.TASK_NOTIFY -> {
                    schedule(notificationInfo, NotificationPriority.HIGH)
                }
                NotificationType.NOTE_NOTIFY -> {
                    schedule(notificationInfo, NotificationPriority.HIGH)
                }
                NotificationType.DECK_NOTIFY -> {
                    schedule(notificationInfo, NotificationPriority.HIGH)
                }
                NotificationType.APP_UPDATE -> {
                    schedule(notificationInfo, NotificationPriority.MEDIUM)
                }
                NotificationType.COMMON_NOTIFY -> {
                    schedule(notificationInfo, NotificationPriority.MEDIUM)
                }
            }
        }

    }

    override fun scheduleBulkNotification(notificationsInfo: List<NotificationInfo>) {
        notificationsInfo.forEach { notificationInfo -> scheduleNotification(notificationInfo) }
    }

    private suspend fun schedule(
        notificationInfo: NotificationInfo,
        notificationPriority: NotificationPriority
    ){
        Log.i("NOTIFICATION_FLOW", "NotificationSchedulerImpl: schedule: priority : ${notificationPriority}")

        if (notificationPriority == NotificationPriority.HIGH){
            Log.i("NOTIFICATION_FLOW", "NotificationSchedulerImpl: schedule: priority : On high priority")
            appStoreRepository.insertNotification(notificationInfo)
            scheduleWithAlarmManager(notificationInfo)
            scheduleWithWorkManager(notificationInfo)
        }else{
            Log.i("NOTIFICATION_FLOW", "NotificationSchedulerImpl: schedule: priority : On medium priority")
            scheduleWithAlarmManager(notificationInfo)
            scheduleWithWorkManager(notificationInfo)
        }
    }

    /**
     * AlarmManager: Fast, precise, but can be killed
     * Used as primary mechanism for exact timing
     */
    private fun scheduleWithAlarmManager(notification: NotificationInfo) {
        try {
            val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
                action = ACTION_SHOW_NOTIFICATION
                putExtra(EXTRA_NOTIFICATION_ID, notification.taskId)
                putExtra(EXTRA_TITLE, notification.taskTitle)
                putExtra(EXTRA_MESSAGE, notification.taskSubTitle)
                putExtra(EXTRA_TYPE, NotificationTypeI.getTypeString(notification.notificationType))
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notification.taskId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Use setAndAllowWhileIdle for reliable delivery even in doze mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+: Use exact alarms with SCHEDULE_EXACT_ALARM permission
                try {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notification.scheduledTime,
                        pendingIntent
                    )
                } catch (e: SecurityException) {
                    Log.w(tag, "NotificationSchedulerImpl:: SCHEDULE_EXACT_ALARM permission not available, using inexact")
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notification.scheduledTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notification.scheduledTime,
                    pendingIntent
                )
            }

            Log.d(tag, "NotificationSchedulerImpl :: AlarmManager scheduled for: ${notification.taskId}")
        } catch (e: Exception) {
            Log.e(tag, "NotificationSchedulerImpl :: Failed to schedule with AlarmManager", e)
        }
    }

    /**
     * WorkManager: Guaranteed delivery with device restart support
     * Primary mechanism for reliability across restarts
     */
    private fun scheduleWithWorkManager(notification: NotificationInfo) {
        try {
            Log.w(tag, "NotificationSchedulerImpl :: scheduleWithWorkManager")

            val delay = notification.scheduledTime - System.currentTimeMillis()

            if (delay < 0) {
                Log.w(tag, "NotificationSchedulerImpl :: Notification time is in the past, showing immediately")
                showNotificationImmediately(notification)
                return
            }

            val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.MINUTES)
                .addTag(WORK_TAG_NOTIFICATIONS)
                .addTag(notification.taskId.toString()) // For individual cancellation
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false) // Allow even on low battery
                        .setRequiresDeviceIdle(false)
                        .build()
                )
                .setInputData(
                    androidx.work.workDataOf(
                        KEY_NOTIFICATION_ID to notification.taskId,
                        KEY_TITLE to notification.taskTitle,
                        KEY_MESSAGE to notification.taskSubTitle,
                        KEY_SCHEDULED_TIME to notification.scheduledTime,
                        KEY_TYPE to NotificationTypeI.getTypeString(notification.notificationType)
                    )
                )
                .build()

            // Use KEEP_EXISTING_WORK if scheduling same notification again
            workManager.enqueueUniqueWork(
                "notification_${notification.taskId}",
                ExistingWorkPolicy.KEEP,
                notificationWork
            )

            Log.d(tag, "NotificationSchedulerImpl :: WorkManager scheduled for: ${notification.taskId}")
        } catch (e: Exception) {
            Log.e(tag, "NotificationSchedulerImpl :: Failed to schedule with WorkManager", e)
        }
    }

    /**
     * Cancel a scheduled notification before it's shown
     */
    fun cancelNotification(notificationId: String) {
        Log.d(tag, "NotificationSchedulerImpl :: Canceling notification: $notificationId")

        // Cancel AlarmManager
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

        // Cancel WorkManager
        workManager.cancelUniqueWork("notification_$notificationId")

        // Update database status
        scope.launch {
            appStoreRepository.updateNotificationStatus(notificationId, NotificationStatus.CANCELLED)
        }
    }

    /**
     * Show notification immediately (used for past times or instant notifications)
     */
    internal fun showNotificationImmediately(notification: NotificationInfo) {
        Log.d(tag, "NotificationSchedulerImpl :: Showing notification immediately: ${notification.taskId}")
        NotificationBroadcastReceiver().showNotification(context, notification)
    }

    /**
     * Reschedule all pending notifications after device restart
     * Called from BootBroadcastReceiver
     */
    internal fun rescheduleAllNotifications() {
        Log.d(tag, "NotificationSchedulerImpl :: Rescheduling all notifications after boot")

        scope.launch {
            val pendingNotifications = appStoreRepository.getPendingNotifications()
            for (notification in pendingNotifications) {
                val timeUntilScheduled = notification.scheduledTime - System.currentTimeMillis()

                // Only reschedule if not yet triggered
                if (timeUntilScheduled > 0) {
                    Log.d(tag, "Rescheduling: ${notification.taskId}")
                    scheduleWithAlarmManager(notification)
                    scheduleWithWorkManager(notification)
                } else if (notification.notificationStatus == NotificationStatus.ACTIVE) {
                    // If scheduled time has passed, show immediately
                    showNotificationImmediately(notification)
                }
            }
        }


    }

    /**
     * Get notification history with optional filtering
     */
    internal suspend fun getNotificationHistory(
        limit: Int = 50,
        status: NotificationStatus? = null
    ): List<NotificationInfo> {
        return if (status != null) {
            appStoreRepository.getNotificationsByStatus(status, limit)
        } else {
            appStoreRepository.getAllNotifications(limit)
        }
    }



    companion object {
        const val ACTION_SHOW_NOTIFICATION = "com.example.notifications.SHOW_NOTIFICATION"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_TYPE = "type"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_DATA = "data"
        const val WORK_TAG_NOTIFICATIONS = "notifications"
        const val KEY_NOTIFICATION_ID = "notification_id"
        const val KEY_TITLE = "title"
        const val KEY_TYPE = "title"
        const val KEY_MESSAGE = "message"
        const val KEY_SCHEDULED_TIME = "scheduled_time"
    }
}