package com.darkzodiak.kontrol.statistics.domain

import com.darkzodiak.kontrol.core.domain.App
import java.time.LocalDate

data class DailyAppUsage(
    val date: LocalDate,
    val app: App,
    val foregroundTimeMs: Long = 0L,
    val percentOfTotalUsage: Int = 0
)