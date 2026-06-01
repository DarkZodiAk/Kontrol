package com.darkzodiak.kontrol.statistics.domain

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface StatisticsRepository {
    fun initializeStatisticsPeriodicSync()
    fun updateAllStatistics()
    fun updateStatisticsForToday()
    fun getAppUsagesForDateRange(
        start: LocalDate, end: LocalDate
    ): Flow<List<DailyUsageReport>>
}