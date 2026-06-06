package com.darkzodiak.kontrol.monitor_block

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.darkzodiak.kontrol.permission.PermissionObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MonitorService: AccessibilityService(), AppCloser {

    @Inject
    lateinit var permissionObserver: PermissionObserver
    @Inject
    lateinit var appBlocker: AppBlocker

    private var isRunning = false
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var deviceLauncher = ""

    override fun onCreate() {
        super.onCreate()
        appBlocker.setAppCloser(this)
        deviceLauncher = getCurrentLauncherPackageName()
        monitorPermission()
        listenForSystemNavigation()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        permissionObserver.updateAllPermissions()

        val currentApp = rootInActiveWindow?.packageName?.toString()
        if (currentApp != null) {
            sendEvent(ExternalEvent.OpenApp(currentApp))
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (isRunning.not()) return
        when(event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                try {
                    processAppEvent(event.packageName.toString())
                } catch (e: Exception) {
                    Log.e("Kontrol log", "Failed to process opened window", e)
                    Toast.makeText(this, "Kontrol: failed to process opened window", Toast.LENGTH_LONG).show()
                }
            }
            else -> Unit
        }
    }

    override fun onInterrupt() { }

    override fun onUnbind(intent: Intent?): Boolean {
        permissionObserver.updateAccessibilityPermission()
        if(isRunning) stop()
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

    private fun listenForSystemNavigation() {
        val lambda = { sendEvent(ExternalEvent.ReturnToLauncher) }
        CloseDialogReceiver(lambda, lambda).register(this)
    }

    private fun monitorPermission() {
        permissionObserver.canRunService.onEach { canRunService ->
            isRunning = canRunService
        }.launchIn(scope)
    }

    private fun processAppEvent(packageName: String) {
        if (packageName in ignoredPackages) return

        when(packageName) {
            deviceLauncher -> sendEvent(ExternalEvent.ReturnToLauncher)
            BLOCKER_PACKAGE_NAME -> sendEvent(ExternalEvent.OpenKontrol)
            else -> sendEvent(ExternalEvent.OpenApp(packageName))
        }
    }

    private fun sendEvent(event: ExternalEvent) {
        scope.launch {
            ExternalEventBus.postEvent(event)
        }
    }

    private fun getCurrentLauncherPackageName(): String {
        val intent = Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)
        }
        return packageManager
            .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)!!
            .activityInfo
            .packageName
    }

    companion object {
        private const val BLOCKER_PACKAGE_NAME = "com.darkzodiak.kontrol"

        private val ignoredPackages = setOf(
            "com.android.systemui",
            "com.google.android.inputmethod.latin",
            "com.android.inputmethod.latin"
        )
    }
}