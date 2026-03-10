package com.designlife.orchestrator.notification.clickmanager

import android.content.Context
import android.content.Intent


public interface TaskListener {
    fun onUserNotificationEvent(id : Int,title : String)
}