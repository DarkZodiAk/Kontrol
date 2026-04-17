package com.darkzodiak.kontrol.statistics.domain

import com.darkzodiak.kontrol.statistics.presentation.Week
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAppUsageForWeekUseCase @Inject constructor(
    private val repository: StatisticsRepository
) {
    operator fun invoke(week: Week): Flow<List<DailyUsageReport>> {
        return repository
            .getAppUsagesForDateRange(week.firstDate, week.lastDate)
            .map {
                if (it.size == 7) return@map it
                addEmptyReportsToMatchWeek(it, week)
            }
    }

    private fun addEmptyReportsToMatchWeek(reports: List<DailyUsageReport>, week: Week): List<DailyUsageReport> {
        val newReports = mutableListOf<DailyUsageReport>()
        var date = week.firstDate
        while (date <= week.lastDate) {
            val report = reports.find { it.date == date }
            if (report != null) {
                newReports.add(report)
            } else {
                newReports.add(DailyUsageReport(date = date))
            }
            date = date.plusDays(1)
        }
        return newReports
    }
}