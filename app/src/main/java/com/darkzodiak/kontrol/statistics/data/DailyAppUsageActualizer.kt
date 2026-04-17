package com.darkzodiak.kontrol.statistics.data

import com.darkzodiak.kontrol.permission.data.PermissionObserver
import com.darkzodiak.kontrol.statistics.data.local.DailyAppUsageDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyAppUsageActualizer @Inject constructor(
    private val dao: DailyAppUsageDao,
    private val permissionObserver: PermissionObserver,
    private val usageGenerator: DailyAppUsageGenerator
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun actualizeAll() = safeLaunch {
        var date = LocalDate.now()
        usageGenerator.initialized.first { it }

        repeat(MAX_ACTUALIZE_PERIOD_DAYS) {
            updateUsagesForDate(date)
            date = date.minusDays(1)
        }
    }

    fun actualizeToday() = safeLaunch {
        val date = LocalDate.now()
        usageGenerator.initialized.first { it }
        updateUsagesForDate(date)
    }

    private fun safeLaunch(block: suspend CoroutineScope.() -> Unit) = scope.launch {
        try {
            block()
        } catch (e: SecurityException) {
            permissionObserver.updateUsageStatsPermission()
        }
    }

    private suspend fun updateUsagesForDate(date: LocalDate) {
        usageGenerator.getDailyAppUsagesForDay(date).forEach { usage ->
            dao.upsertUsage(usage)
        }
    }

    companion object {
        private const val MAX_ACTUALIZE_PERIOD_DAYS = 7
    }
}
