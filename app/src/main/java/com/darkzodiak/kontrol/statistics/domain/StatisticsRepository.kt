package com.darkzodiak.kontrol.statistics.domain

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface StatisticsRepository {
    fun updateStatistics()
    fun updateStatisticsForToday()
    fun getAppUsagesForDateRange(
        start: LocalDate, end: LocalDate
    ): Flow<List<DailyUsageReport>>
}