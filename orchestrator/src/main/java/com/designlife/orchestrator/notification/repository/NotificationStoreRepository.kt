package com.designlife.orchestrator.notification.repository

import com.designlife.orchestrator.notification.data.NotificationInfo
import com.designlife.orchestrator.notification.store.NotificationStore
import kotlinx.serialization.InternalSerializationApi


class NotificationStoreRepository(
    private val notificationStore: NotificationStore
) {
    suspend fun storeNotifications(notificationInfoList : List<NotificationInfo>){
        notificationInfoList.forEach { notificationInfo ->
            notificationStore.storeNotification(notificationInfo)
        }
    }

    suspend fun deleteNotification(notificationInfo: NotificationInfo){
        notificationStore.deleteNotificationByTime(notificationInfo)
    }

    suspend fun readAllNotification() : List<NotificationInfo> = notificationStore.readAllNotification()

    suspend fun cleanNotificationStore() = notificationStore.clearStore()
}