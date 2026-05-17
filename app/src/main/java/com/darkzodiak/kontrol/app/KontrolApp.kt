package com.darkzodiak.kontrol.app

import android.app.Application
import com.darkzodiak.kontrol.apps.domain.AppRepository
import com.darkzodiak.kontrol.monitor_block.KontrolService
import com.darkzodiak.kontrol.permission.data.PermissionObserver
import com.darkzodiak.kontrol.profile.domain.ProfileRepository
import com.darkzodiak.kontrol.statistics.domain.StatisticsRepository
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
    lateinit var appRepository: AppRepository
    @Inject
    lateinit var profileRepository: ProfileRepository
    @Inject
    lateinit var statisticsRepository: StatisticsRepository

    override fun onCreate() {
        super.onCreate()
        permissionObserver.updateAllPermissions()
        appRepository.syncInstalledApps()
        profileRepository.actualizeAllProfiles()
        statisticsRepository.initializeStatisticsPeriodicSync()
        permissionObserver.canRunService.onEach { canRunService ->
            if (canRunService) {
                startService(KontrolService.buildActionIntent(this, KontrolService.ACTION_START))
            }
        }.launchIn(applicationScope)
    }
}