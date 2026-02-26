package com.designlife.orchestrator.notification.data

import java.util.Date

data class NotificationInfo(
    val date : Date,
    val taskTitle : String,
    val taskSubTitle : String,
    val taskId : Int
)
