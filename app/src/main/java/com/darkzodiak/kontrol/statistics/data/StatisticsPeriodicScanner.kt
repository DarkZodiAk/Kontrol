package com.darkzodiak.kontrol.statistics.data

import android.os.Handler
import android.os.Looper
import com.darkzodiak.kontrol.permission.data.PermissionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsPeriodicScanner @Inject constructor(
    private val permissionObserver: PermissionObserver,
    private val dailyAppUsageActualizer: DailyAppUsageActualizer
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())

    private var permissionScanJob: Job? = null

    private var hasUsageStatsPermission = false
    private var hasScheduledScan = false
    private var isFirstScan = true

    fun initialize() {
        permissionScanJob?.cancel()
        permissionScanJob = permissionObserver.permissionsState.onEach { state ->
            hasUsageStatsPermission = state.hasUsageStatsPermission
            if (hasUsageStatsPermission && hasScheduledScan.not()) scan()
        }.launchIn(scope)
    }

    private fun scan() {
        if (hasUsageStatsPermission.not()) return

        if (isFirstScan) {
            dailyAppUsageActualizer.actualizeAll()
            isFirstScan = false
        } else {
            dailyAppUsageActualizer.actualizeToday()
        }

        val runnable = Runnable {
            hasScheduledScan = false
            scan()
        }
        handler.postDelayed(runnable, PERIOD)
        hasScheduledScan = true
    }

    companion object {
        private const val PERIOD: Long = 1000 * 60 * 60 * 12
    }
}