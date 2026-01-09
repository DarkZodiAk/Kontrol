package com.darkzodiak.kontrol.core

import android.app.Application
import com.darkzodiak.kontrol.core.data.AppScanner
import com.darkzodiak.kontrol.core.data.KontrolService
import com.darkzodiak.kontrol.core.data.receivers.DynamicReceiverRegistrator
import com.darkzodiak.kontrol.permission.data.PermissionObserver
import com.darkzodiak.kontrol.profile.data.ProfileActualizer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltAndroidApp
class KontrolApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var permissionObserver: PermissionObserver
    @Inject
    lateinit var appScanner: AppScanner
    @Inject
    lateinit var profileActualizer: ProfileActualizer

    override fun onCreate() {
        super.onCreate()
        appScanner.updateAll()
        DynamicReceiverRegistrator(this).register()
        profileActualizer.actualizeAll()
        permissionObserver.canRunService.onEach { canRunService ->
            if(canRunService) {
                startService(KontrolService.buildActionIntent(this, KontrolService.ACTION_START))
            }
        }.launchIn(applicationScope)
        permissionObserver.updateAllPermissions()
    }
}