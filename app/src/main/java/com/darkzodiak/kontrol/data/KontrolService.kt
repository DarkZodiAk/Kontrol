package com.darkzodiak.kontrol.data

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.darkzodiak.kontrol.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KontrolService: AccessibilityService() {
    //    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }

    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }
    private val activityManager by lazy {
        getSystemService<ActivityManager>()!!
    }
    private var scope = CoroutineScope(Dispatchers.Main)
    private val baseNotification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Kontrol")
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_launcher_foreground)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> {
                createNotificationChannel()
                startForeground(1, buildNotification("null"))
                updateNotification()
            }
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    override fun onServiceConnected() {
        Log.d("ACCESSIBILITY", "Connected")
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            flags = AccessibilityServiceInfo.DEFAULT
            notificationTimeout = 250
        }

        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        //Log.d("ACCESSIBILITY", "$event")
        when(event?.eventType) {
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                Log.d("ACCESSIBILITY", "WINDOWS_CHANGED")
            }
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Log.d("ACCESSIBILITY", "WINDOWS_STATE_CHANGED")
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Log.d("ACCESSIBILITY", "WINDOWS_CONTENT_CHANGED")
            }
            else -> Unit
        }
    }

    override fun onInterrupt() {
        Log.d("ACCESSIBILITY", "Interrupted")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("ACCESSIBILITY", "Destroyed")
        return super.onUnbind(intent)
    }


    private fun stop() {
        stopSelf()
        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main)
    }

    private fun buildNotification(appName: String): Notification {
        return baseNotification.setContentText(appName).build()
    }

    private fun updateNotification() {
        scope.launch {
            while(true) {
                notificationManager.notify(1, buildNotification("null"))
                delay(1250L)
            }
        }
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Kontrol",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "kontrol_channel"

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        fun buildActionIntent(context: Context, action: String): Intent {
            return Intent(context, KontrolService::class.java).also {
                it.action = action
            }
        }
    }
}