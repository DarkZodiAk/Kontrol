package com.darkzodiak.kontrol.core.presentation.delayDialog

import java.time.LocalDateTime

data class DelayDialogState(
    val delayTime: LocalDateTime = LocalDateTime.now(),
    val selectDelayTime: LocalDateTime = LocalDateTime.now(),
    val delayType: DelayType = DelayType.CUSTOM,
    val unsavedDelayType: DelayType = DelayType.CUSTOM
)
