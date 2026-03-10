package com.designlife.orchestrator

import android.content.Context

class SchedulingEngine(
    private val context: Context
) {
    private var notificationScheduler : NotificationSchedulerImpl? = null
    fun notificationScheduler() : NotificationScheduler{
        if (notificationScheduler == null){
            synchronized(this){
                if (notificationScheduler == null){
                    notificationScheduler = NotificationSchedulerImpl(context)
                }
            }
        }
        return notificationScheduler!!
    }
}