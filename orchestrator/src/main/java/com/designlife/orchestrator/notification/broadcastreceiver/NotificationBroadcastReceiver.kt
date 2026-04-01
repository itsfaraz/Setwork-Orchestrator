package com.designlife.orchestrator.notification.broadcastreceiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.designlife.orchestrator.NotificationSchedulerImpl
import com.designlife.orchestrator.R
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType
import com.designlife.orchestrator.data.NotificationTypeI
import com.designlife.orchestrator.notification.NotificationServiceLocator
import com.designlife.orchestrator.notification.clickmanager.NotificationClickManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Broadcast receiver triggered by AlarmManager or system boot
 * Handles notification display and event logging
 */
internal class NotificationBroadcastReceiver : BroadcastReceiver() {

    private val tag = "NotificationBroadcastReceiver"
    private val scope = NotificationServiceLocator.provideCoroutineScope()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        when (intent.action) {
            // Triggered by AlarmManager
            NotificationSchedulerImpl.ACTION_SHOW_NOTIFICATION -> {
                handleShowNotification(context, intent)
            }
            // Triggered by system boot
            Intent.ACTION_BOOT_COMPLETED -> {
                handleBootCompleted(context)
            }
            // Triggered when timezone changes
            Intent.ACTION_TIMEZONE_CHANGED -> {
                handleTimezoneChanged(context)
            }
        }
    }

    /**
     * Display the notification and log delivery
     */
    private fun handleShowNotification(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(NotificationSchedulerImpl.EXTRA_NOTIFICATION_ID,0)
        val title = intent.getStringExtra(NotificationSchedulerImpl.EXTRA_TITLE) ?: "Notification"
        val message = intent.getStringExtra(NotificationSchedulerImpl.EXTRA_MESSAGE) ?: ""
        val type = NotificationTypeI.getType(intent.getStringExtra(NotificationSchedulerImpl.EXTRA_TYPE) ?: "")

        Log.d(tag, "Displaying notification: $notificationId")

        val notification = NotificationInfo(
            taskId = notificationId.toInt(),
            taskTitle = title,
            taskSubTitle = message,
            notificationType = type,
            scheduledTime = System.currentTimeMillis(),
            notificationStatus = NotificationStatus.DELIVERED,
            createdTime = System.currentTimeMillis(),
            deliveredTime = System.currentTimeMillis()
        )
        showNotification(context, notification)
    }

    /**
     * Core notification display logic
     * Uses NotificationCompat for backward compatibility
     */
    fun showNotification(context: Context, notification: NotificationInfo) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            SETWORK_CHANNEL_ID = getChannelId(notification.notificationType)
            // Create notification channel for Android 8+
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = android.app.NotificationChannel(
                    SETWORK_CHANNEL_ID,
                    "Important Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Time-critical notifications"
                    enableVibration(true)
                    setShowBadge(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Build notification
            val onNotificationClick = NotificationClickManager.getNotificationIntent(context,notification.taskId,notification.taskTitle,notification.notificationType,classPath)
            val notificationBuilder = NotificationCompat.Builder(context, SETWORK_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_setwork_notification)
                .setContentTitle(notification.taskTitle)
                .setContentText(notification.taskSubTitle)
                .setColorized(true)
                .setContentIntent(onNotificationClick)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVibrate(longArrayOf(0, 500, 250, 500))
                .setLights(Color.BLUE, 500, 2000)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))

            // Show notification
            notificationManager.notify(
                notification.taskId.hashCode(),
                notificationBuilder.build()
            )

            // Log delivery in database
            val notificationStore = NotificationServiceLocator.provideAppStoreRepository(context)

            scope.launch(Dispatchers.IO) {
                notificationStore.updateNotificationStatus(
                    id = notification.taskId.toString(),
                    status = NotificationStatus.DELIVERED,
                )
            }

            Log.d(tag, "Notification shown successfully: ${notification.taskId} : ${notification.taskTitle}")

        } catch (e: Exception) {
            Log.e(tag, "Failed to show notification", e)
        }
    }

    /**
     * Handle device boot by rescheduling all pending notifications
     * Requires RECEIVE_BOOT_COMPLETED permission
     */
    private fun handleBootCompleted(context: Context) {
        Log.d(tag, "Device booted, rescheduling notifications")

        // Run in background to avoid ANR
        try {
            val scheduler = NotificationServiceLocator.provideNotificationScheduler(context)
            scheduler.rescheduleAllNotifications()
            Log.d(tag, "Boot rescheduling completed")
        } catch (e: Exception) {
            Log.e(tag, "Error during boot rescheduling", e)
        }
    }

    /**
     * Handle timezone changes by recalculating scheduled times if needed
     */
    private fun handleTimezoneChanged(context: Context) {
        Log.d(tag, "Timezone changed, validating schedules")

        try {
            val notificationStore = NotificationServiceLocator.provideAppStoreRepository(context)
            scope.launch {
                val pendingNotifications = notificationStore.getPendingNotifications()

                // For timezone-aware notifications, reschedule them
                // (Implementation depends on whether your app uses local or UTC times)
                for (notification in pendingNotifications) {
                    Log.d(tag, "Validating: ${notification.taskId}")
                }
            }

        } catch (e: Exception) {
            Log.e(tag, "Error handling timezone change", e)
        }
    }

    companion object {
        internal var SETWORK_CHANNEL_ID = "notification_channel_important"
        internal const val SETWORK_TASK_CHANNEL = "SETWORK_CHANNEL_TASK_ID"
        internal const val SETWORK_NOTE_CHANNEL = "SETWORK_CHANNEL_NOTE_ID"
        internal const val SETWORK_DECK_CHANNEL = "SETWORK_CHANNEL_DECK_ID"
        internal const val SETWORK_DELIVERY_CHANNEL = "SETWORK_CHANNEL_DELIVERY_ID"

        var classPath =  "com.designlife.justdo.MainActivity"
//        var classPath =  "com.designlife.justdo_orchestrator.MainActivity"


        fun getChannelId(notificationType: NotificationType) : String{
            return when(notificationType){
                NotificationType.APP_UPDATE -> SETWORK_DELIVERY_CHANNEL
                NotificationType.COMMON_NOTIFY -> SETWORK_CHANNEL_ID
                NotificationType.NOTE_NOTIFY -> SETWORK_NOTE_CHANNEL
                NotificationType.TASK_NOTIFY -> SETWORK_TASK_CHANNEL
                NotificationType.DECK_NOTIFY-> SETWORK_DECK_CHANNEL
            }
        }
    }
}