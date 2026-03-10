package com.designlife.orchestrator

import android.content.Context
import com.designlife.orchestrator.data.NotificationInfo

interface NotificationScheduler {
    fun scheduleNotification(notificationInfo: NotificationInfo)
    fun scheduleBulkNotification(notificationsInfo: List<NotificationInfo>)
}