package com.designlife.orchestrator.notification.clickmanager

import android.content.Context
import android.content.Intent
import android.util.Log

object NotificationClickManager {
    private lateinit var notificationTaskListener : TaskListener

    public fun setListener(notificationTaskListener : TaskListener){
        this.notificationTaskListener = notificationTaskListener
    }

    public fun notifyListener(id : Int,title : String,classPath : String){
        try {
            if (classPath.isNotEmpty()){
                if (!::notificationTaskListener.isInitialized){
                    Log.i("NOTIFICATION_FLOW", "notifyListener: Intialized Main Task Listener")
                    notificationTaskListener = Class.forName(classPath).newInstance() as TaskListener
                }
                Log.i("NOTIFICATION_FLOW", "notifyListener: notifyListener")
                notificationTaskListener.onBroadCastRecieveListener(id,title)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}