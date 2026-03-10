package com.designlife.orchestrator.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType

@Entity
internal data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,
    val scheduledTime: Long,
    val taskTitle : String,
    val taskSubTitle : String,
    val taskId : Int,
    val notificationType : String,
    val createdAt : Long,
    val deliveredAt : Long,
    val status : String
){
    internal fun toModel() : NotificationInfo {
        return NotificationInfo(
            scheduledTime = this.scheduledTime,
            taskTitle = this.taskTitle,
            taskSubTitle = this.taskSubTitle,
            taskId = this.taskId,
            notificationType = getNotificationType(this.notificationType),
            notificationStatus = getNotificationStatus(this.status),
            createdTime = this.createdAt,
            deliveredTime = this.deliveredAt
        )
    }

    private fun getNotificationStatus(status: String): NotificationStatus {
        return when(status){
            "In_ACTIVE" -> NotificationStatus.In_ACTIVE
            "ACTIVE" -> NotificationStatus.ACTIVE
            "CANCELLED" -> NotificationStatus.CANCELLED
            "ON_RETRY" -> NotificationStatus.ON_RETRY
            "DELIVERED" -> NotificationStatus.DELIVERED
            else -> NotificationStatus.In_ACTIVE
        }
    }

    private fun getNotificationType(notificationType: String): NotificationType {
        return when(notificationType){
            "TASK_NOTIFY" -> NotificationType.TASK_NOTIFY
            "NOTE_NOTIFY" -> NotificationType.NOTE_NOTIFY
            "DECK_NOTIFY" -> NotificationType.DECK_NOTIFY
            "APP_UPDATE" -> NotificationType.APP_UPDATE
            else -> NotificationType.COMMON_NOTIFY
        }
    }
}
