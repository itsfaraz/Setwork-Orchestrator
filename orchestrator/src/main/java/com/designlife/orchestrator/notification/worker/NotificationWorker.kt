package com.designlife.orchestrator.notification.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.designlife.orchestrator.NotificationSchedulerImpl
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType
import com.designlife.orchestrator.data.NotificationTypeI
import com.designlife.orchestrator.notification.NotificationServiceLocator
import com.designlife.orchestrator.notification.broadcastreceiver.NotificationBroadcastReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    private val tag = "NotificationWorker"
    private val notificationStore = NotificationServiceLocator.provideAppStoreRepository(context)
    private val scope = NotificationServiceLocator.provideCoroutineScope()
    
    override fun doWork(): Result {
        return try {

            Log.d(tag, "Starting notification work")
            
            val notificationId = inputData.getInt(NotificationSchedulerImpl.KEY_NOTIFICATION_ID,0)
                ?: return Result.failure()
            
            val title = inputData.getString(NotificationSchedulerImpl.KEY_TITLE)
                ?: "Notification"
            val message = inputData.getString(NotificationSchedulerImpl.KEY_MESSAGE)
                ?: ""
            val scheduledTime = inputData.getLong(
                NotificationSchedulerImpl.KEY_SCHEDULED_TIME,
                System.currentTimeMillis()
            )
            val type = inputData.getString(NotificationSchedulerImpl.KEY_TYPE)
                ?: ""

            scope.launch(Dispatchers.Main.immediate) {
                // Check if notification should still be delivered

                val notification = notificationStore.getNotificationById(notificationId.toString())
                if (notification != null && notification.notificationStatus == NotificationStatus.CANCELLED) {
                    Log.d(tag, "Notification cancelled, skipping: $notificationId")
                }else{
                    // Create fresh notification model
                    val notificationModel = NotificationInfo(
                        taskId = notificationId.toInt(),
                        taskTitle = title,
                        taskSubTitle = message,
                        notificationType = NotificationTypeI.getType(type),
                        scheduledTime = scheduledTime,
                        notificationStatus = NotificationStatus.DELIVERED,
                        createdTime = System.currentTimeMillis(),
                        deliveredTime = System.currentTimeMillis()
                    )

                    // Display notification
                    NotificationBroadcastReceiver().showNotification(applicationContext, notificationModel)
                }
            }

            Log.d(tag, "Notification delivered successfully: $notificationId")
            Result.success()
            
        } catch (e: Exception) {
            Log.e(tag, "Error delivering notification, will retry", e)
            // Retry with backoff (configured in NotificationScheduler)
            Result.retry()
        }
    }
}