package com.darkzodiak.kontrol.statistics.presentation

import com.darkzodiak.kontrol.statistics.domain.DailyUsageReport
import java.time.LocalDate

data class StatisticsState(
    val selectedWeek: Week = Week.currentWeek(),
    val isCurrentWeek: Boolean = true,
    val dailyReports: List<DailyUsageReport> = DEFAULT_REPORTS,
    val focusedReportIndex: Int = 0,
    val hasUsageStatsPermission: Boolean = false
) {
    companion object {
        private val DEFAULT_REPORTS = buildList { repeat(7) {
            add(DailyUsageReport(LocalDate.now(), 0L, emptyList()))
        } }
    }
}
