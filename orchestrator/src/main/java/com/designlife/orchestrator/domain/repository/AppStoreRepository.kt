package com.designlife.orchestrator.domain.repository

import android.content.Context
import android.util.Log
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.room.NotificationDao
import com.designlife.orchestrator.data.room.NotificationEntity
import com.designlife.orchestrator.notification.NotificationServiceLocator

internal class AppStoreRepository(
    private val notificationDao: NotificationDao
) {

    suspend fun insertNotification(notificationInfo: NotificationInfo){
        try {
            Log.i("NOTIFICATION_FLOW", "AppStoreRepository: insertNotification : ${notificationInfo.taskTitle}")
            val entity = notificationInfo.toEntity()
            notificationDao.insertNotification(entity)
        } catch (e: Exception) {
            android.util.Log.e("NotificationStore", "Insert failed", e)
        }
    }

    suspend fun updateNotificationStatus(id: String, status: NotificationStatus){
        try {
            val existing = notificationDao.getNotificationById(id)
            Log.i("NOTIFICATION_FLOW", "AppStoreRepository: updateNotificationStatus : Id - ${id} : Existing Title - ${existing?.taskTitle} : Task Id : ${existing?.taskId}")
            existing?.let {
                val updated = existing.copy(
                    status = status.name,
                    deliveredAt = if (status == NotificationStatus.DELIVERED)
                        System.currentTimeMillis() else existing.deliveredAt
                )
                notificationDao.updateNotification(updated)
            }
        } catch (e: Exception) {
            android.util.Log.e("AppStoreRepository", "Update failed", e)
        }
    }

    suspend fun getNotificationById(id : String) : NotificationInfo? {
        var result : NotificationEntity? = null
        try {
            result = notificationDao.getNotificationById(id)
        } catch (e: Exception) {
            android.util.Log.e("AppStoreRepository", "Get by ID failed", e)
            null
        }
        return result?.toModel()
    }

    suspend fun getPendingNotifications() : List<NotificationInfo>{
        return try {
            val entities = mutableListOf<NotificationEntity>()
            val result = notificationDao.getPendingNotifications(System.currentTimeMillis())
            entities.addAll(result)
            entities.map { it.toModel() }
        } catch (e: Exception) {
            android.util.Log.e("AppStoreRepository", "Get pending failed", e)
            emptyList()
        }

    }

    suspend fun getNotificationsByStatus(status: NotificationStatus, limit: Int): List<NotificationInfo> {
        return try {
            val entities = mutableListOf<NotificationEntity>()
            val result = notificationDao.getNotificationsByStatus(status.name,limit)
            entities.addAll(result)
            entities.map { it.toModel() }
        } catch (e: Exception) {
            android.util.Log.e("AppStoreRepository", "Get by status failed", e)
            emptyList()
        }
    }

    suspend fun getAllNotifications(limit: Int): List<NotificationInfo> {
        return try {
            val entities = mutableListOf<NotificationEntity>()
            val result = notificationDao.getAllNotifications(limit)
            entities.addAll(result)
            entities.map { it.toModel() }
        } catch (e: Exception) {
            android.util.Log.e("NotificationStore", "Get all failed", e)
            emptyList()
        }
    }

    suspend fun deleteOlderThan(timestamp: Long): Int {
        return try {
            val count = intArrayOf(0)
            count[0] = notificationDao.deleteOlderThan(timestamp)
            count[0]
        } catch (e: Exception) {
            android.util.Log.e("NotificationStore", "Delete failed", e)
            0
        }

    }


}