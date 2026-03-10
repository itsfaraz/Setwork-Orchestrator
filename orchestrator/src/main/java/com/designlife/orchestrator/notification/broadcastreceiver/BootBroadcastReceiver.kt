package com.designlife.orchestrator.notification.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Specialized receiver for boot events with higher priority
 * Some devices require this separate receiver for reliable boot handling
 */
internal class BootBroadcastReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        
        Log.d("BootBroadcastReceiver", "Boot completed")
        
        // Delegate to NotificationBroadcastReceiver
        val notificationReceiver = NotificationBroadcastReceiver()
        notificationReceiver.onReceive(context, intent)
    }
}