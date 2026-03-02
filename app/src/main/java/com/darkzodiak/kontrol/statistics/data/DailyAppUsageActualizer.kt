package com.darkzodiak.kontrol.statistics.data

import com.darkzodiak.kontrol.statistics.data.local.DailyAppUsageDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyAppUsageActualizer @Inject constructor(
    private val dao: DailyAppUsageDao,
    private val usageGenerator: DailyAppUsageGenerator
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun actualizeAll() = scope.launch {
        var date = LocalDate.now()
        usageGenerator.initialized.first { it }

        repeat(7) {
            updateUsagesForDate(date)
            date = date.minusDays(1)
        }
    }

    fun actualizeToday() = scope.launch {
        val date = LocalDate.now()
        updateUsagesForDate(date)
    }

    private suspend fun updateUsagesForDate(date: LocalDate) {
        usageGenerator.getDailyAppUsagesForDay(date).forEach { usage ->
            dao.upsertUsage(usage)
        }
    }
}
