package com.darkzodiak.kontrol.core.data

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.darkzodiak.kontrol.block.AppBlocker
import com.darkzodiak.kontrol.block.AppCloser
import com.darkzodiak.kontrol.external_events.ExternalEvent
import com.darkzodiak.kontrol.external_events.ExternalEventBus
import com.darkzodiak.kontrol.permission.data.PermissionObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KontrolService: AccessibilityService(), AppCloser {

    @Inject
    lateinit var appFetcher: AppFetcher
    @Inject
    lateinit var permissionObserver: PermissionObserver
    @Inject
    lateinit var appBlocker: AppBlocker

    private var isRunning = false
    private val scope = CoroutineScope(Dispatchers.Main)

    private var deviceLauncher = ""

    // TODO(): Maybe relocate and move package names into constants
    private val ignoredPackages = listOf(
        "com.android.systemui",
        "com.google.android.inputmethod.latin",
        "com.android.inputmethod.latin"
    )


    override fun onCreate() {
        super.onCreate()
        appBlocker.setAppCloser(this)
        deviceLauncher = appFetcher.getCurrentLauncherPackageName()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> {
                isRunning = true
            }
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        permissionObserver.updateAllPermissions()

        val currentApp = rootInActiveWindow.packageName.toString()
        sendEvent(ExternalEvent.OpenApp(currentApp))
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (isRunning.not()) return
        when(event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                processAppEvent(event.packageName.toString())
            }
            else -> Unit
        }
    }

    override fun onInterrupt() { }

    override fun onUnbind(intent: Intent?): Boolean {
        scope.launch {
            permissionObserver.updateAccessibilityPermission()
            if(isRunning) stop()
        }
        return super.onUnbind(intent)
    }

    private fun stop() {
        stopSelf()
        isRunning = false
        scope.coroutineContext.cancelChildren()
    }

    override fun closeApp(packageName: String) {
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    private fun processAppEvent(packageName: String) {
        if (packageName in ignoredPackages) return

        if (packageName == deviceLauncher) {
            sendEvent(ExternalEvent.ReturnToLauncher)
        } else {
            sendEvent(ExternalEvent.OpenApp(packageName))
        }
    }

    private fun sendEvent(event: ExternalEvent) {
        scope.launch {
            ExternalEventBus.postEvent(event)
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        fun buildActionIntent(context: Context, action: String): Intent {
            return Intent(context, KontrolService::class.java).also {
                it.action = action
            }
        }
    }
}