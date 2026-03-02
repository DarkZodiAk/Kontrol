package com.darkzodiak.kontrol.statistics.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.permission.data.PermissionObserver
import com.darkzodiak.kontrol.statistics.domain.DailyUsageReport
import com.darkzodiak.kontrol.statistics.domain.GetAppUsageForWeekUseCase
import com.darkzodiak.kontrol.statistics.domain.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAppUsageForWeekUseCase: GetAppUsageForWeekUseCase,
    private val repository: StatisticsRepository,
    private val permissionObserver: PermissionObserver
): ViewModel() {

    private var appUsagesJob: Job? = null

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
                updateStatistics()
            }
            StatisticsAction.ShowPreviousWeek -> {
                val prevWeek = state.selectedWeek.prevWeek()
                updateState { copy(selectedWeek = prevWeek, isCurrentWeek = false) }
                updateStatistics()
            }
            StatisticsAction.BackToCurrentWeek -> {
                if (state.isCurrentWeek) return
                updateState { copy(selectedWeek = Week.currentWeek(), isCurrentWeek = true) }
                updateStatistics()
            }
            is StatisticsAction.ChangeDay -> {
                val index = action.reportIndex.coerceIn(0, 6)
                updateState { copy(focusedReport = dailyReports[index], focusedReportIndex = index) }
            }
            StatisticsAction.ReturnedToScreen -> {
                updateStatistics()
            }
        }
    }

    private fun updateStatistics() {
        repository.updateStatisticsForToday()
    }

    private fun onWeekOrPermissionChange() {
        appUsagesJob?.cancel()
        if (state.hasUsageStatsPermission.not()) return
        appUsagesJob = getAppUsageForWeekUseCase(state.selectedWeek).onEach {
            updateState { copy(dailyReports = it) }
            updateIndexOfCurrentDay(it)
        }.launchIn(viewModelScope)
    }

    private fun updateIndexOfCurrentDay(reports: List<DailyUsageReport>) {
        val currentDate = LocalDate.now()
        val index = reports.indexOfLast { it.date == currentDate }
        val focusedReportIndex = if (index == -1) 0 else index
        val focusedReport = reports[focusedReportIndex]
        updateState { copy(
            focusedReport = focusedReport,
            focusedReportIndex = focusedReportIndex
        ) }
    }

    private fun updateState(update: StatisticsState.() -> StatisticsState) {
        val week = state.selectedWeek
        val permission = state.hasUsageStatsPermission

        state = update(state)

        if (week != state.selectedWeek || permission != state.hasUsageStatsPermission) {
            onWeekOrPermissionChange()
        }
    }
}