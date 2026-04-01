package com.designlife.orchestrator.notification.clickmanager

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.designlife.orchestrator.data.NotificationType
import com.designlife.orchestrator.data.NotificationTypeI

object NotificationClickManager {
    internal fun getNotificationIntent(context: Context,notificationId : Int, title : String, type: NotificationType, classPath : String) : PendingIntent?{
        try {
            if (classPath.isNotEmpty()){
                Log.i("NOTIFICATION_FLOW", "NotificationClickManager:: getNotificationIntent")

                val activity : Activity = Class.forName(classPath).newInstance() as Activity
                activity?.let {
                    val intent = Intent(context, activity::class.java).apply {
                        putExtra("fromNotification", true)
                        putExtra("notificationId", notificationId)
                        putExtra("title", title)
                        putExtra("type", NotificationTypeI.getTypeString(type))
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    Log.i("NOTIFICATION_FLOW", "NotificationClickManager:: getNotificationIntent : pending intent")
                    return PendingIntent.getActivity(
                        context,
                        notificationId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
        Log.i("NOTIFICATION_FLOW", "NotificationClickManager:: getNotificationIntent : pending intent = null")
        return null
    }
}