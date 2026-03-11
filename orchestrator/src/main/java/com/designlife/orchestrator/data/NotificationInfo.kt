package com.designlife.orchestrator.data

import com.designlife.orchestrator.data.room.NotificationEntity

public data class NotificationInfo(
    val scheduledTime: Long = 0L,
    val taskTitle : String = "",
    val taskSubTitle : String = "",
    val taskId : Int = 0,
    val notificationType : NotificationType = NotificationType.TASK_NOTIFY,
    val createdTime : Long = 0L,
    val deliveredTime : Long = 0L,
    val notificationStatus : NotificationStatus = NotificationStatus.In_ACTIVE
){
    override fun toString(): String {
        return "Type : ${notificationType}, Task Title : ${taskTitle}, Time : ${scheduledTime}, Subtitle : ${taskSubTitle}, Status : ${notificationStatus} \n"
    }

    internal fun toEntity() : NotificationEntity{
        return NotificationEntity(
            scheduledTime = this.scheduledTime,
            taskTitle = this.taskTitle,
            taskSubTitle = this.taskSubTitle,
            taskId = this.taskId,
            notificationType = this.notificationType.name,
            status = this.notificationStatus.name,
            createdAt = this.createdTime,
            deliveredAt = this.deliveredTime
        )
    }
}
