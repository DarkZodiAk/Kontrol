package com.darkzodiak.kontrol.statistics.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.darkzodiak.kontrol.statistics.presentation.Week

@Composable
fun WeekRangeBar(
    week: Week,
    isCurrentWeek: Boolean,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(onClick = onPrevWeek) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
            )
        }
        Text(
            text = week.formatRange(),
            fontSize = 16.sp
        )
        IconButton(onClick = onNextWeek, enabled = isCurrentWeek.not()) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun WeerRangeBarPreview() {
    Scaffold(Modifier.fillMaxSize()) {
        WeekRangeBar(
            week = Week.currentWeek().prevWeek().prevWeek(),
            isCurrentWeek = false,
            onNextWeek = {},
            onPrevWeek = {}
        )
    }
}