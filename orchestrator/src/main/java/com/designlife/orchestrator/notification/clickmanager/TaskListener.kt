package com.designlife.orchestrator.notification.clickmanager


public interface TaskListener {
    fun onUserNotificationEvent(id : Int,title : String, type : String)
}