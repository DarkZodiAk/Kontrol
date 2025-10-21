package com.darkzodiak.kontrol.data

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.darkzodiak.kontrol.data.permission.PermissionObserver
import com.darkzodiak.kontrol.domain.KontrolRepository
import com.darkzodiak.kontrol.overlay.OverlayData
import com.darkzodiak.kontrol.overlay.OverlayManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KontrolService: AccessibilityService() {

    @Inject
    lateinit var repository: KontrolRepository
    @Inject
    lateinit var appObserver: AppObserver
    @Inject
    lateinit var permissionObserver: PermissionObserver
    @Inject
    lateinit var overlayManager: OverlayManager


    private var isRunning = false
    private var currentApp = BLOCKER_APP_ID
    private val scope = CoroutineScope(Dispatchers.Main)

    private var currentLauncher = ""
    private val ignoredPackages = listOf("com.android.systemui", "com.google.android.inputmethod.latin")


    override fun onCreate() {
        super.onCreate()
        currentLauncher = appObserver.getCurrentLauncherPackageName()
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
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        when(event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                processAppEvent("${event.packageName}")
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

    private fun processAppEvent(packageName: String) {
        if(isRunning && packageName != currentApp && packageName !in ignoredPackages) {
            currentApp = packageName
            scope.launch {
                if(repository.isAppInProfiles(currentApp)) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    overlayManager.openOverlay(OverlayData.SimpleBlock(packageName)) {}
                    currentApp = currentLauncher
                }
            }
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        private const val BLOCKER_APP_ID = "com.darkzodiak.kontrol"

        fun buildActionIntent(context: Context, action: String): Intent {
            return Intent(context, KontrolService::class.java).also {
                it.action = action
            }
        }
    }
}