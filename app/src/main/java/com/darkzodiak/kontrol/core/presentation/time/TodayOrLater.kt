package com.darkzodiak.kontrol.core.presentation.time

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
object TodayOrLater: SelectableDates {
    private const val DAY_OFFSET_MILLIS = 24 * 60 * 60 * 1000

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis >= System.currentTimeMillis() - DAY_OFFSET_MILLIS
    }

    override fun isSelectableYear(year: Int): Boolean {
        val currentYear = LocalDate.now().year
        return year >= currentYear
    }
}