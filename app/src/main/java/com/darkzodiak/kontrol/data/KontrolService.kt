package com.darkzodiak.kontrol.data

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.darkzodiak.kontrol.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KontrolService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

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
                startForeground(1, buildNotification(getRunningApp()))
                updateNotification()
            }
            ACTION_STOP -> stop()
        }
        return START_STICKY
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
                notificationManager.notify(1, buildNotification(getRunningApp()))
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

    private fun getRunningApp(): String {
        //Log.d("SERVICE", activityManager.runningAppProcesses.map { it.processName }.toString())
        Log.d("SERVICE", activityManager.getRunningTasks(10).map { it.baseActivity?.packageName }.toString())

        return "null"
        //return activityManager
            //.getRunningTasks(10)
            //.firstOrNull { it. }
            //.firstOrNull { it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE }
            //.toString()
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