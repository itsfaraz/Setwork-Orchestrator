package com.designlife.justdo_orchestrator

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.orchestrator.NotificationScheduler
import com.designlife.orchestrator.SchedulingEngine
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationType
import com.designlife.orchestrator.notification.clickmanager.TaskListener
import java.util.Date
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() , TaskListener {
    private lateinit var scheduler: NotificationScheduler

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduler = SchedulingEngine(this).notificationScheduler()
//        scheduleNotifications()
        setContent {
            var timeMillis by remember {
                mutableStateOf("")
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    value = timeMillis,
                    onValueChange = { value : String->
                        timeMillis = value
                    },
                    placeholder = {
                        Text("Schedule time in minutes : ex - 5")
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = {
                    scheduleNotificationByTime(timeMillis)
                    timeMillis = ""

                }) {
                    Text("Trigger", color = Color.Green)
                }
            }

        }

    }

    private fun scheduleNotificationByTime(minutes : String){
        try{
            var time = System.currentTimeMillis()
            if (minutes.equals("0")){
                time  = System.currentTimeMillis() + 50
            }else{
                time  = System.currentTimeMillis() + ((minutes.toInt() * 60 ) * 1000)
            }
            val notificationInfo = NotificationInfo(scheduledTime = time,taskTitle = "Notification Test",taskSubTitle = "Scheduled Notification : Set trigger time ${minutes} minutes",taskId = System.currentTimeMillis().hashCode().absoluteValue, notificationType = NotificationType.TASK_NOTIFY, createdTime = System.currentTimeMillis())
            scheduler.scheduleNotification(notificationInfo)
            val format: SimpleDateFormat = SimpleDateFormat("hh:mm aa")
            val formattedTime: String? = format.format(Date(time))
            Toast.makeText(this, "Scheduled Timed Notification Of ${formattedTime}", Toast.LENGTH_SHORT).show()
        }catch (e : Exception){
            Toast.makeText(this, "Enter Integer Value : example : 10", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleNotifications() {
        val date = Date(1773153939938)
        val secondDate = Date(date.time + (60*1000))
        val thirdDate = Date(secondDate.time + (60*1000))
        val fourthDate = Date(thirdDate.time + (2*60*1000))
        val fifthDate = Date(fourthDate.time + (2*60*1000))


        val notificationInfo1 = NotificationInfo(scheduledTime = date.time,taskTitle = "Daily DSA Learning",taskSubTitle = "Learning and improving",taskId = 1, notificationType = NotificationType.TASK_NOTIFY)
        val notificationInfo2 = NotificationInfo(scheduledTime = secondDate.time,taskTitle = "Daily exercise agenda",taskSubTitle = "Exercise| Fun| Health",taskId = 2, notificationType = NotificationType.TASK_NOTIFY)
        val notificationInfo3 = NotificationInfo(scheduledTime = thirdDate.time,taskTitle = "Brisk walk reminder",taskSubTitle = "Jogging",taskId = 3, notificationType = NotificationType.TASK_NOTIFY)
        val notificationInfo4 = NotificationInfo(scheduledTime = fourthDate.time,taskTitle = "Device Reboot - Test Check 1",taskSubTitle = "Device Restart Check",taskId = 4, notificationType = NotificationType.TASK_NOTIFY)
        val notificationInfo5 = NotificationInfo(scheduledTime = fifthDate.time,taskTitle = "Device Reboot - Test Check 2",taskSubTitle = "Device Restart Check",taskId = 5, notificationType = NotificationType.TASK_NOTIFY)

        Log.i("NOTIFICATION_FLOW", "MainActivity : Started Scheduling")
        scheduler.scheduleBulkNotification(listOf(notificationInfo1,notificationInfo2,notificationInfo3,notificationInfo4,notificationInfo5))
        Toast.makeText(this@MainActivity, "All Notifications Scheduled", Toast.LENGTH_SHORT).show()
    }


    override fun onUserNotificationEvent(id: Int, title: String) {
        Log.i("NOTIFICATION_FLOW", "onUserNotificationEvent: $id")
    }

}