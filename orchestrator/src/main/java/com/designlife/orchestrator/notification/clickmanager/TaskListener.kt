package com.designlife.orchestrator.notification.clickmanager

import android.content.Context
import android.content.Intent


interface TaskListener {
    fun onBroadCastRecieveListener(id : Int,title : String)
}