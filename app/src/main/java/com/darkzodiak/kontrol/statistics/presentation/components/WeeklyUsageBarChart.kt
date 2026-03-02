package com.darkzodiak.kontrol.statistics.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.darkzodiak.kontrol.statistics.domain.DailyUsageReport
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun WeeklyUsageBarChart(
    reports: List<DailyUsageReport>,
    focusedReportIndex: Int?,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusedIndex = focusedReportIndex?.coerceIn(0, 6)
    val maxValue = remember(reports) {
        reports.maxOfOrNull { it.totalUsageTimeMs } ?: 0L
    }
    val chartHeight = 200.dp
    val yAxisWidth = 30.dp
    val xAxisHeight = 20.dp
    val barWidth = 28.dp

    val totalHeight = chartHeight + xAxisHeight

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight)
    ) {
        YAxisLabels(
            maxValue = maxValue,
            height = chartHeight,
            width = yAxisWidth
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stepCount = 4
                    val stepHeight = size.height / stepCount

                    repeat(stepCount) { step ->
                        if (step == 0) return@repeat
                        drawLine(
                            color = color,
                            start = Offset(0f, stepHeight * step),
                            end = Offset(size.width, stepHeight * step),
                            strokeWidth = 2f
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    reports.forEachIndexed { index, report ->
                        val value = report.totalUsageTimeMs
                        val barHeight = if (maxValue > 0) {
                            (value.toFloat() / maxValue * chartHeight.value).dp
                        } else {
                            0.dp
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            BarItem(
                                barHeight = barHeight,
                                isFocused = index == focusedIndex,
                                onClick = { onDayClick(index) },
                                barWidth = barWidth
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(xAxisHeight)
            ) {
                reports.forEach { report ->
                    Text(
                        text = getDayLabel(report.date.dayOfWeek),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun YAxisLabels(
    maxValue: Long,
    height: Dp,
    width: Dp
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .width(width)
            .height(height)
    ) {
        (0..4).forEach { step ->
            val valueMs = maxValue * (4 - step) / 4
            Text(
                text = formatTime(valueMs),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BarItem(
    barHeight: Dp,
    isFocused: Boolean,
    onClick: () -> Unit,
    barWidth: Dp,
) {
    val backgroundColor = if (isFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize().clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(barHeight)
                .align(Alignment.BottomCenter)
                .background(backgroundColor, MaterialTheme.shapes.small)
        )
    }
}

private fun formatTime(valueMs: Long): String {
    return when {
        valueMs < 1000 -> "0с"
        valueMs < 60000 -> {
            val seconds = valueMs / 1000.0
            formatTimeValue(seconds, "с")
        }
        valueMs < 3600000 -> {
            val minutes = valueMs / 60000.0
            formatTimeValue(minutes, "м")
        }
        else -> {
            val hours = valueMs / 3600000.0
            formatTimeValue(hours, "ч")
        }
    }
}

private fun formatTimeValue(value: Double, unit: String): String {
    return when {
        value < 10 -> {
            val formatted = "%.1f".format(value)
            if (formatted.endsWith(".0")) formatted.dropLast(2) else formatted
        }
        else -> {
            value.toInt().toString()
        }
    } + unit
}


private fun getDayLabel(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.SUNDAY -> "Вс"
        DayOfWeek.MONDAY -> "Пн"
        DayOfWeek.TUESDAY -> "Вт"
        DayOfWeek.WEDNESDAY -> "Ср"
        DayOfWeek.THURSDAY -> "Чт"
        DayOfWeek.FRIDAY -> "Пт"
        DayOfWeek.SATURDAY -> "Сб"
    }
}

@Composable
@Preview
fun UsageScreen() {
    val sampleData = remember {
        (0..6).map { dayOffset ->
            DailyUsageReport(
                date = LocalDate.now().with(DayOfWeek.SUNDAY).plusDays(dayOffset.toLong()),
                appUsages = emptyList(),
                totalUsageTimeMs = (30 + (50 * Math.random())).toInt() * 60000L
            )
        }
    }
    Scaffold { padding ->
        WeeklyUsageBarChart(
            reports = sampleData,
            focusedReportIndex = null,
            onDayClick = {},
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        )
    }
}