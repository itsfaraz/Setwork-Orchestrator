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
import com.designlife.orchestrator.notification.data.NotificationInfo
import com.designlife.orchestrator.notification.repository.TaskNotificationRepository
import kotlinx.coroutines.runBlocking
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
        taskRepository = NotificationServiceLocator.provideNotificationRepository(this@MainActivity,alarmManager)
        NotificationClickManager.setListener(this)
        scheduleNotifications()
    }

    private fun scheduleNotifications(){
        val date = Date(System.currentTimeMillis())
        val secondDate = Date(date.time + (60*1000))
        val thirdDate = Date(secondDate.time + (60*1000))
        val fourthDate = Date(thirdDate.time + (2*60*1000))
        val fifthDate = Date(fourthDate.time + (2*60*1000))


        val notificationInfo1 = NotificationInfo(time = date.time,taskTitle = "Daily DSA Learning",taskSubTitle = "Learning and improving",taskId = 1)
        val notificationInfo2 = NotificationInfo(time = secondDate.time,taskTitle = "Daily exercise agenda",taskSubTitle = "Exercise| Fun| Health",taskId = 2)
        val notificationInfo3 = NotificationInfo(time = thirdDate.time,taskTitle = "Brisk walk reminder",taskSubTitle = "Jogging",taskId = 3)
        val notificationInfo4 = NotificationInfo(time = fourthDate.time,taskTitle = "Device Reboot - Test Check 1",taskSubTitle = "Device Restart Check",taskId = 4)
        val notificationInfo5 = NotificationInfo(time = fifthDate.time,taskTitle = "Device Reboot - Test Check 2",taskSubTitle = "Device Restart Check",taskId = 5)
        runBlocking {
            taskRepository.scheduleNotification(listOf(notificationInfo1,notificationInfo2,notificationInfo3,notificationInfo4,notificationInfo5))
            Toast.makeText(this@MainActivity, "All Notifications Scheduled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTimeString(epoch : Long) : String{
        val format = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        return format.format(Date(epoch))
    }

    override fun onUserNotificationEvent(id: Int, title: String) {
        Log.i("NOTIFICATION_FLOW", "onUserNotificationEvent: $id")
    }

}