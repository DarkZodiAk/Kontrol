package com.darkzodiak.kontrol.statistics.data

import com.darkzodiak.kontrol.core.data.local.dao.DailyAppUsageDao
import com.darkzodiak.kontrol.statistics.domain.DailyUsageReport
import com.darkzodiak.kontrol.statistics.domain.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt

class StatisticsRepositoryImpl @Inject constructor(
    private val dailyAppUsageDao: DailyAppUsageDao,
    private val dailyAppUsageMapper: DailyAppUsageMapper,
    private val dailyAppUsageActualizer: DailyAppUsageActualizer,
    private val statisticsPeriodicUpdater: StatisticsPeriodicUpdater
): StatisticsRepository {
    override fun initializeStatisticsSync() {
        statisticsPeriodicUpdater.initialize()
    }

    override fun updateAllStatistics() {
        dailyAppUsageActualizer.actualizeAll()
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
                    val totalUsageTime = usages.sumOf { it.foregroundTimeMs }

                    val appUsagesWithPercent = usages
                        .sortedByDescending { it.foregroundTimeMs }
                        .map { usage ->
                            val percent = if (totalUsageTime > 0) {
                                ((usage.foregroundTimeMs * 100.0) / totalUsageTime).roundToInt()
                            } else 0
                            usage.copy(percentOfTotalUsage = percent)
                        }

                    DailyUsageReport(
                        date = date,
                        totalUsageTimeMs = totalUsageTime,
                        appUsages = appUsagesWithPercent
                    )
                }.sortedBy { it.date }
        }
    }
}