package com.darkzodiak.kontrol.statistics.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.darkzodiak.kontrol.statistics.presentation.components.AppUsage
import com.darkzodiak.kontrol.statistics.presentation.components.DayUsageInfoCard
import com.darkzodiak.kontrol.statistics.presentation.components.NeedsUsageStatsPermissionBanner
import com.darkzodiak.kontrol.statistics.presentation.components.WeekRangeBar
import com.darkzodiak.kontrol.statistics.presentation.components.WeeklyUsageBarChart

@Composable
fun StatisticsScreenRoot(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    LifecycleResumeEffect(Unit) {
        viewModel.onAction(StatisticsAction.ReturnedToScreen)
        onPauseOrDispose {  }
    }

    if (viewModel.state.hasUsageStatsPermission) {
        StatisticsScreen(
            state = viewModel.state,
            onAction = viewModel::onAction
        )
    } else {
        NeedsUsageStatsPermissionBanner()
    }
}

@Composable
fun StatisticsScreen(
    state: StatisticsState,
    onAction: (StatisticsAction) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            WeekRangeBar(
                week = state.selectedWeek,
                isCurrentWeek = state.isCurrentWeek,
                onPrevWeek = { onAction(StatisticsAction.ShowPreviousWeek) },
                onNextWeek = { onAction(StatisticsAction.ShowNextWeek) }
            )
        }

        item {
            WeeklyUsageBarChart(
                reports = state.dailyReports,
                focusedReportIndex = state.focusedReportIndex,
                onDayClick = { onAction(StatisticsAction.ChangeDay(it)) },
                modifier = Modifier.padding(8.dp)
            )
        }

        item {
            DayUsageInfoCard(
                usageTimeMs = state.dailyReports[state.focusedReportIndex].totalUsageTimeMs,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 16.dp)
            )
        }

        items(state.dailyReports[state.focusedReportIndex].appUsages) { usage ->
            AppUsage(
                usage = usage,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun StatisticsScreenPreview() {
    Scaffold { padding ->
        StatisticsScreen(
            state = StatisticsState(),
            onAction = {}
        )
    }
}