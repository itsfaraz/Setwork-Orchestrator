package com.designlife.orchestrator.notification.clickmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log

internal object NotificationClickManager {

    private lateinit var notificationTaskListener : TaskListener
    private var activity : Any? = null


    public fun setListener(notificationTaskListener : TaskListener){
        this.notificationTaskListener = notificationTaskListener
    }

    public fun notifyListener(id : Int, title : String, classPath : String){
        try {
            if (classPath.isNotEmpty()){
                if (activity == null){
                    activity = Class.forName(classPath).newInstance() as Activity
                }
                notificationTaskListener = activity as TaskListener
                Log.i("NOTIFICATION_FLOW", "notifyListener: notifyListener")
                if ((activity as Activity).intent.getBooleanExtra("fromNotification",false)){
                    Log.i("NOTIFICATION_FLOW", "onCreate: User Clicked Notification")
                    notificationTaskListener.onUserNotificationEvent(id,title)
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}