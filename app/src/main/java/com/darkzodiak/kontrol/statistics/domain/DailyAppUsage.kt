package com.darkzodiak.kontrol.statistics.domain

import com.darkzodiak.kontrol.core.data.local.entity.App

data class DailyAppUsage(
    val app: App,
    val foregroundTimeMs: Long,
    val percentOfTotalUsage: Long
)