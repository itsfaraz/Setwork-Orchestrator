package com.designlife.orchestrator.presentation.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object UtilConstant {
    val TASK_ID = "TASK_ID"
    val TASK_TITLE = "TASK_TITLE"
    val TASK_CONTENT = "TASK_CONTENT"
    val TASK_TIME = "TASK_TIME"


    fun getGracefullyDateFromDate(epoch: Long): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date(epoch)
        val dateFormat = SimpleDateFormat("EEE dd MMM yyyy", Locale.ENGLISH)
        return dateFormat.format(calendar.time)
    }

    fun getGracefullyTimeFromEpoch(epoch: Long): String {
        val date = Date(epoch) // Convert epoch value from seconds to milliseconds
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.US)
        return dateFormat.format(date)
    }

}
