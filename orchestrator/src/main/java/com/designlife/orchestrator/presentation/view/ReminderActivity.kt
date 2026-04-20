package com.designlife.orchestrator.presentation.view

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.orchestrator.R
import com.designlife.orchestrator.data.NotificationType
import com.designlife.orchestrator.notification.broadcastreceiver.NotificationBroadcastReceiver.Companion.classPath
import com.designlife.orchestrator.notification.clickmanager.NotificationClickManager
import com.designlife.orchestrator.presentation.utils.UtilConstant
import com.designlife.orchestrator.ui.themes.DismissButtonColor
import com.designlife.orchestrator.ui.themes.DismissButtonIconColor
import com.designlife.orchestrator.ui.themes.PrimaryColorHome1
import com.designlife.orchestrator.ui.themes.PrimaryColorHome2

class ReminderActivity : ComponentActivity() {

    private var taskId : Int = 0
    private var taskTitle : String = ""
    private var taskContent : String = ""
    private var taskTime : Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationArgsDateSet()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, null)
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        setContent {
            ReminderScreen(
                taskTitle = taskTitle,
                taskDescription = taskContent,
                taskDate = getFormattedDate(taskTime),
                taskTime = getFormattedTime(taskTime),
                onDismiss = {
                    val notifId = taskId.hashCode()
                    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.cancel(notifId)
                    finish()
                },
                onView = {

                    val notifId = taskId.hashCode()
                    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.cancel(notifId)
                    finish()

                    NotificationClickManager
                        .startSetworkTaskIntent(this,taskId,taskTitle, NotificationType.TASK_NOTIFY,classPath)
                }
            )
        }
    }

    private fun navigationArgsDateSet() {
        taskId = intent.getIntExtra(UtilConstant.TASK_ID,0)
        taskTitle = intent.getStringExtra(UtilConstant.TASK_TITLE) ?: ""
        taskContent = intent.getStringExtra(UtilConstant.TASK_CONTENT) ?: ""
        taskTime = intent.getLongExtra(UtilConstant.TASK_TIME,0L)
    }

    fun getFormattedDate(epoch : Long) : String{
        return UtilConstant.getGracefullyDateFromDate(epoch)
    }

    fun getFormattedTime(epoch : Long) : String{
        return UtilConstant.getGracefullyTimeFromEpoch(epoch)
    }

}


@Composable
fun ReminderScreen(
    taskTitle : String,
    taskDescription: String,
    taskDate : String,
    taskTime : String,
    onDismiss: () -> Unit,
    onView : () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.Companion.verticalGradient(
                    colors =listOf(
                        PrimaryColorHome2.value,
                        PrimaryColorHome1.value
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(76.dp))
            Image(modifier = Modifier.size(38.dp), painter = painterResource(R.drawable.ic_app_logo), contentDescription = "Reminder App Logo")
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Setwork -", color = Color.Black, fontSize = 28.sp)
                Text(text = " Task", color = Color.White, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(120.dp))
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.fillMaxWidth(.2F))
                Icon(modifier = Modifier.size(14.dp), painter = painterResource(R.drawable.ic_title), contentDescription = "Reminder Title")
                Spacer(modifier = Modifier.width(8.dp))
                Text(modifier = Modifier.fillMaxWidth(.8F), text = taskTitle, color = Color.White, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (taskDescription.isNotEmpty()){
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.fillMaxWidth(.2F))
                    Icon(modifier = Modifier.size(14.dp), painter = painterResource(R.drawable.ic_description), contentDescription = "Reminder Description")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(modifier = Modifier.fillMaxWidth(.8F),text = taskDescription, color = Color.White, fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.fillMaxWidth(.2F))
                Icon(modifier = Modifier.size(15.dp), painter = painterResource(R.drawable.ic_date), contentDescription = "Reminder Date")
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = taskDate, color = Color.White, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = taskTime, color = Color.White, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.wrapContentSize(),
                    onClick = {onView()},
                    colors = ButtonDefaults.buttonColors(containerColor = DismissButtonColor)
                ) {
                    Text(text = "Open", color = Color.White, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(modifier = Modifier.size(10.dp), painter = painterResource(R.drawable.ic_expand), contentDescription = "Reminder Dismiss", tint = DismissButtonIconColor)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color = DismissButtonColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {onDismiss()}) {
                        Icon(modifier = Modifier.size(24.dp), painter = painterResource(R.drawable.ic_dismiss), contentDescription = "Reminder Dismiss", tint = DismissButtonIconColor)
                    }
                }
            }
        }
    }
}