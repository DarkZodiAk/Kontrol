package com.darkzodiak.kontrol.data

import android.accessibilityservice.AccessibilityService
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
import com.darkzodiak.kontrol.domain.KontrolRepository
import com.darkzodiak.kontrol.overlay.OverlayManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KontrolService: AccessibilityService() {

    @Inject
    lateinit var repository: KontrolRepository
    @Inject
    lateinit var appObserver: AppObserver
    private var overlayManager: OverlayManager? = null

    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    private var isRunning = false
    private var currentApp = "com.darkzodiak.kontrol"
    private var scope = CoroutineScope(Dispatchers.Main)

    private var currentLauncher = ""
    private val ignoredPackages = listOf("com.android.systemui", "com.google.android.inputmethod.latin")
    private val baseNotification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Kontrol")
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_launcher_foreground)


    override fun onCreate() {
        super.onCreate()
        currentLauncher = appObserver.getCurrentLauncherPackageName()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> {
                isRunning = true
                overlayManager = OverlayManager(context = this)
                createNotificationChannel()
                startForeground(1, buildNotification(currentApp))
            }
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        when(event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Log.d("WINDOW CHANGED", event.packageName.toString())
                processAppEvent("${event.packageName}")
            }
            else -> Unit
        }
    }

    override fun onInterrupt() {
        Log.d("ACCESSIBILITY", "Interrupted")
    }


    private fun stop() {
        stopSelf()
        isRunning = false
        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main)
    }

    private fun processAppEvent(packageName: String) {
        if(isRunning && packageName != currentApp && packageName !in ignoredPackages) {
            currentApp = packageName
            scope.launch {
                if(repository.isAppInProfiles(currentApp)) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    overlayManager?.open("$packageName заблокирован")
                    currentApp = currentLauncher
                }
                notificationManager.notify(1, buildNotification(currentApp))
            }
        }
    }

    private fun buildNotification(appName: String): Notification {
        return baseNotification.setContentText(appName).build()
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