package com.designlife.orchestrator.notification.broadcastreceiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.designlife.orchestrator.R

class TaskService(
    private val context : Context
) {

    companion object{
        const val TODO_WIZ_CHANNEL = "SETWORK_CHANNEL_ID"
//        var classPath =  "com.designlife.justdo.MainActivity"
        var classPath =  "com.designlife.justdo_orchestrator.MainActivity"
    }

    fun showNotification(
        todoTitle : String,
        todoSubTitle : String

    ){
        var activity : Any = Class.forName(classPath).newInstance()
        val intent = Intent(context,activity::class.java)
        Log.i("NOTIFICATION_FLOW", "showNotification: ${activity.toString()} ${activity.hashCode()}")
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = NotificationCompat
            .Builder(context, TODO_WIZ_CHANNEL)
            .setSmallIcon(R.drawable.baseline_adjust_24)
            .setContentTitle(todoTitle)
            .setContentText(todoSubTitle)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1,notification.build())
    }

    fun cancelNotification(
        uniqueId : Int,
        alarmManager: AlarmManager
    ){
        val intent = Intent(context, TaskReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, uniqueId,intent,PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }
}