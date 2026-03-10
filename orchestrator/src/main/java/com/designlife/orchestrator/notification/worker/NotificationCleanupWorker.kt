package com.designlife.orchestrator.notification.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.designlife.orchestrator.notification.NotificationServiceLocator
import kotlinx.coroutines.launch

/**
 * Background task worker for cleanup of old notification history
 * Keeps database from growing indefinitely
 */
internal class NotificationCleanupWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    private val tag = "FLOW"
    private val notificationStore = NotificationServiceLocator.provideAppStoreRepository(context)
    private val scope = NotificationServiceLocator.provideCoroutineScope()

    override fun doWork(): Result {
        return try {
            Log.d(tag, "NotificationCleanupWorker:: Starting cleanup")
            
            // Delete notifications older than 30 days
            val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
//            val thirtyDaysAgo = System.currentTimeMillis() - (2 * 60 * 1000) // Test 2 minutes
            scope.launch {
                val deletedCount = notificationStore.deleteOlderThan(thirtyDaysAgo)
                Log.d(tag, "NotificationCleanupWorker:: Deleted $deletedCount old notifications")
            }

            Result.success()
            
        } catch (e: Exception) {
            Log.e(tag, "NotificationCleanupWorker:: Cleanup failed", e)
            Result.retry()
        }
    }
}