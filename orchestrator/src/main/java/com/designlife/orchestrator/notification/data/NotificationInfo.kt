package com.designlife.orchestrator.notification.data

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class NotificationInfo(
    val time: Long = 0L,
    val taskTitle : String = "",
    val taskSubTitle : String = "",
    val taskId : Int = 0
){
    override fun toString(): String {
        return "Task Title : ${taskTitle}, Time : ${time}, Subtitle : ${taskSubTitle} \n"
    }
}
