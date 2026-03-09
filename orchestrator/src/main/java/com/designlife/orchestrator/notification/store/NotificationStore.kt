package com.designlife.orchestrator.notification.store

import android.content.Context
import androidx.datastore.dataStore
import com.designlife.orchestrator.notification.data.NotificationInfo
import kotlinx.coroutines.flow.first

class NotificationStore(
    private val context: Context
) {
    private val Context.myDataStore by dataStore(
        fileName = "notification_store.json",
        serializer = NotificationSerializer
    )

    suspend fun storeNotification(notificationInfo: NotificationInfo) {
        context.myDataStore.updateData { list ->
            (list + notificationInfo)
                .distinctBy { it.taskId }
                .sortedByDescending { it.time ?: 0 }
                .take(50)
        }
    }

    suspend fun readAllNotification(): List<NotificationInfo> {
        return context.myDataStore.data.first()
    }

    suspend fun deleteNotificationByTime(notificationInfo: NotificationInfo) {
        context.myDataStore.updateData { list ->
            list.filterNot { it.taskTitle.equals(notificationInfo.taskTitle) && it.taskSubTitle.equals(notificationInfo.taskSubTitle)  }
        }
    }

    suspend fun clearStore() {
        context.myDataStore.updateData {
            emptyList()
        }
    }
}