package com.darkzodiak.kontrol.statistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun DayUsageInfoCard(
    usageTimeMs: Long,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(
            text = "Время использования:",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = getShortTimeTextFromMs(usageTimeMs),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun getShortTimeTextFromMs(ms: Long): String {
    val seconds = ms / 1000
    val minutes = (seconds / 60) % 60
    val hours = seconds / 3600
    return if (seconds < 60) seconds.toString() + "с"
        else if (hours == 0L) minutes.toString() + "м"
        else if (minutes == 0L) hours.toString() + "ч"
        else hours.toString() + "ч " + minutes.toString() + "м"
}