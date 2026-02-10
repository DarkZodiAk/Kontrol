package com.darkzodiak.kontrol.statistics.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.permission.data.PermissionObserver
import com.darkzodiak.kontrol.statistics.data.WeeklyUsageReportGenerator
import com.darkzodiak.kontrol.statistics.domain.DailyUsageReport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val usageReportGenerator: WeeklyUsageReportGenerator,
    private val permissionObserver: PermissionObserver
): ViewModel() {

    var state by mutableStateOf(StatisticsState())
        private set

    init {
        permissionObserver.permissionsState
            .onEach {
                updateState { copy(hasUsageStatsPermission = it.hasUsageStatsPermission) }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: StatisticsAction) {
        when (action) {
            StatisticsAction.ShowNextWeek -> {
                if (state.isCurrentWeek) return
                val nextWeek = state.selectedWeek.nextWeek()
                updateState { copy(
                    selectedWeek = nextWeek,
                    isCurrentWeek = nextWeek == Week.currentWeek()
                ) }
                updateWeekUsageReports()
            }
            StatisticsAction.ShowPreviousWeek -> {
                val prevWeek = state.selectedWeek.prevWeek()
                updateState { copy(selectedWeek = prevWeek, isCurrentWeek = false) }
                updateWeekUsageReports()
            }
            StatisticsAction.BackToCurrentWeek -> {
                if (state.isCurrentWeek) return
                updateState { copy(selectedWeek = Week.currentWeek(), isCurrentWeek = true) }
                updateWeekUsageReports()
            }

            is StatisticsAction.ChangeDay -> {
                val index = action.reportIndex.coerceIn(0, 6)
                updateState { copy(focusedReportIndex = index) }
            }

            StatisticsAction.ReturnedToScreen -> {
                updateWeekUsageReports()
//                Log.d("Kontrol Statistics", "Returned to screen")
            }
        }
    }

    private fun updateWeekUsageReports() {
        val report = usageReportGenerator.generateReport(state.selectedWeek)
        if (report != null) {
            val reports = generateReportForSelectedWeek()
            updateState { copy(
                dailyReports = reports,
                focusedReportIndex = getIndexOfCurrentDayInReports(reports)
            ) }
        }
    }

    private fun generateReportForSelectedWeek(): List<DailyUsageReport> {
        return usageReportGenerator.generateReport(state.selectedWeek) ?: emptyList()
    }

    private fun getIndexOfCurrentDayInReports(reports: List<DailyUsageReport>): Int {
        val currentDate = LocalDate.now()
        val index = reports.indexOfLast { it.date == currentDate }
        return if (index == -1) 0 else index
    }

    private fun updateState(update: StatisticsState.() -> StatisticsState) {
        state = update(state)
    }
}