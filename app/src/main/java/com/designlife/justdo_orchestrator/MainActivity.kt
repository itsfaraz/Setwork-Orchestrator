package com.designlife.justdo_orchestrator

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.designlife.orchestrator.NotificationServiceLocator
import com.designlife.orchestrator.notification.clickmanager.NotificationClickManager
import com.designlife.orchestrator.notification.clickmanager.TaskListener
import com.designlife.orchestrator.notification.repository.TaskNotificationRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() , TaskListener {
    private lateinit var alarmManager : AlarmManager
    private lateinit var notificationManager : NotificationManager
    private lateinit var taskRepository : TaskNotificationRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        taskRepository = NotificationServiceLocator.provideNotificationRepository(this,alarmManager)
        NotificationClickManager.setListener(this)
        scheduleNotifications()
    }

    private fun scheduleNotifications(){
        val date = Date(System.currentTimeMillis())
        val triplets = mutableListOf<Triple<Date,String,Int>>()

        triplets.add(Triple(date,"Daily DSA Learning",1))
        val secondDate = Date(date.time + (60*1000))
        triplets.add(Triple(secondDate,"Daily exercise agenda",2))
        val thirdDate = Date(secondDate.time + (60*1000))
        triplets.add(Triple(thirdDate,"Brisk walk reminder",3))
        taskRepository.scheduleNotification(triplets)
        Toast.makeText(this, "All Notifications Scheduled", Toast.LENGTH_SHORT).show()
    }

    private fun getTimeString(epoch : Long) : String{
        val format = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        return format.format(Date(epoch))
    }

    override fun onBroadCastRecieveListener(id : Int,title : String) {
        Log.i("NOTIFICATION_FLOW", "onBroadCastRecieveListener: $id")
    }


}