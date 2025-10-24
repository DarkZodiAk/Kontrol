package com.darkzodiak.kontrol

import android.app.Application
import com.darkzodiak.kontrol.data.AppFetcher
import com.darkzodiak.kontrol.data.KontrolService
import com.darkzodiak.kontrol.data.permission.PermissionObserver
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
    lateinit var appFetcher: AppFetcher

    override fun onCreate() {
        super.onCreate() // TODO(): Check how context changes in transition from direct boot mode
        appFetcher.update()
        permissionObserver.canRunService.onEach { canRunService ->
            if(canRunService) {
                startService(KontrolService.buildActionIntent(this, KontrolService.ACTION_START))
            }
        }.launchIn(applicationScope)
        permissionObserver.updateAllPermissions()
    }
}