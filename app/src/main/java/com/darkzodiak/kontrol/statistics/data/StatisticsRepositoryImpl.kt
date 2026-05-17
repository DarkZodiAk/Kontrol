package com.darkzodiak.kontrol.statistics.data

import com.darkzodiak.kontrol.core.data.local.dao.DailyAppUsageDao
import com.darkzodiak.kontrol.statistics.domain.DailyUsageReport
import com.darkzodiak.kontrol.statistics.domain.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val dailyAppUsageDao: DailyAppUsageDao,
    private val dailyAppUsageMapper: DailyAppUsageMapper,
    private val dailyAppUsageActualizer: DailyAppUsageActualizer,
    private val statisticsPeriodicUpdater: StatisticsPeriodicUpdater
): StatisticsRepository {
    override fun initializeStatisticsPeriodicSync() {
        statisticsPeriodicUpdater.initialize()
    }

    override fun updateStatisticsForToday() {
        dailyAppUsageActualizer.actualizeToday()
    }

    override fun getAppUsagesForDateRange(
        start: LocalDate, end: LocalDate
    ): Flow<List<DailyUsageReport>> {
        return dailyAppUsageDao.getUsageForDateRange(start, end).map {
            dailyAppUsageMapper.entityListToDomain(it)
                .groupBy { it.date }
                .map { (date, usages) ->
                    DailyUsageReport(
                        date = date,
                        totalUsageTimeMs = usages.sumOf { it.foregroundTimeMs },
                        appUsages = usages.sortedByDescending { it.foregroundTimeMs }
                    )
                }.sortedBy { it.date }
        }
    }
}