package com.adopshun.render.maintask

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class OnClearFromRecentService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("ClearFromRecentService", "Service Started")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ClearFromRecentService", "Service Destroyed")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.e("ClearFromRecentService", "END")
        //Code here
        RenderPopup.closeAlertDialog()
        if (RenderPopup.sessionManager?.getBooleanValue(AppConstants.IS_POPPED_UP)==true){
            RenderPopup.sessionManager?.setBoolean(AppConstants.IS_COMPLETED,true)
        }
        stopSelf()
    }
}