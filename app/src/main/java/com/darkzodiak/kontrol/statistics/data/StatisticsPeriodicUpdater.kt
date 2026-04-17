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
class StatisticsPeriodicUpdater @Inject constructor(
    private val permissionObserver: PermissionObserver,
    private val dailyAppUsageActualizer: DailyAppUsageActualizer
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var statisticsUpdateJob: Job? = null

    private var hasUsageStatsPermission = false
    private var isFirstScan = true

    fun initialize() {
        permissionObserver.permissionsState.onEach { state ->
            statisticsUpdateJob?.cancel()
            isFirstScan = true
            hasUsageStatsPermission = state.hasUsageStatsPermission
            if (hasUsageStatsPermission) {
                statisticsUpdateJob = scope.launch { scan() }
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
            delay(UPDATE_PERIOD)
        }
    }

    companion object {
        private const val UPDATE_PERIOD: Long = 1000 * 60 * 60 * 12
    }
}