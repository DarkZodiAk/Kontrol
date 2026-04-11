package com.darkzodiak.kontrol.statistics.data

import com.darkzodiak.kontrol.permission.data.PermissionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsPeriodicScanner @Inject constructor(
    private val permissionObserver: PermissionObserver,
    private val dailyAppUsageActualizer: DailyAppUsageActualizer
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var permissionScanJob: Job? = null
    private var statisticsScanJob: Job? = null

    private var hasUsageStatsPermission = false
    private var isFirstScan = true

    fun initialize() {
        permissionScanJob?.cancel()
        permissionScanJob = permissionObserver.permissionsState.onEach { state ->
            statisticsScanJob?.cancel()
            hasUsageStatsPermission = state.hasUsageStatsPermission
            if (hasUsageStatsPermission) {
                statisticsScanJob = scope.launch { scan() }
            }
        }.launchIn(scope)
    }

    private suspend fun scan() {
        while (true) {
            if (isFirstScan) {
                dailyAppUsageActualizer.actualizeAll()
                isFirstScan = false
            } else {
                dailyAppUsageActualizer.actualizeToday()
            }
            delay(PERIOD)
        }
    }

    companion object {
        private const val PERIOD: Long = 1000 * 60 * 60 * 12
    }
}