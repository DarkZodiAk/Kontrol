package com.darkzodiak.kontrol.core.presentation.delayDialog

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class DelayDialogState(
    val delayTime: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
    val selectDelayTime: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
    val delayType: DelayType = DelayType.CUSTOM,
    val unsavedDelayType: DelayType = DelayType.CUSTOM
)
