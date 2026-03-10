package com.designlife.orchestrator.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
internal interface NotificationDao {
    @Insert
    suspend fun insertNotification(notification: NotificationEntity)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)

    @Query("SELECT * FROM NotificationEntity WHERE taskId=:id")
    suspend fun getNotificationById(id: String): NotificationEntity?

    @Query("SELECT * FROM notificationentity WHERE status = :status ORDER BY scheduledTime DESC LIMIT :limit")
    suspend fun getNotificationsByStatus(status: String, limit: Int): List<NotificationEntity>

    @Query("SELECT * FROM notificationentity WHERE status = 'SCHEDULED' AND scheduledTime > :currentTime")
    suspend fun getPendingNotifications(currentTime: Long): List<NotificationEntity>

    @Query("SELECT * FROM notificationentity ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getAllNotifications(limit: Int): List<NotificationEntity>

    @Query("DELETE FROM notificationentity WHERE createdAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long): Int

    @Query("SELECT COUNT(*) FROM notificationentity WHERE status = 'SCHEDULED'")
    suspend fun getPendingCount(): Int

    @Query("SELECT COUNT(*) FROM notificationentity WHERE status = 'DELIVERED'")
    suspend fun getDeliveredCount(): Int
}