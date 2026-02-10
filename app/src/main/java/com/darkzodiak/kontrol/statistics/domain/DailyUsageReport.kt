package com.darkzodiak.kontrol.statistics.domain

import java.time.LocalDate

data class DailyUsageReport(
    val date: LocalDate = LocalDate.now(),
    val totalUsageTimeMs: Long = 0L,
    val appUsages: List<DailyAppUsage> = emptyList()
)
