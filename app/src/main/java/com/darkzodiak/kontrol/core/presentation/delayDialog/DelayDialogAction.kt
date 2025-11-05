package com.darkzodiak.kontrol.core.presentation.delayDialog

import java.time.LocalDateTime

sealed interface DelayDialogAction {
    data class SelectDelayType(val type: DelayType): DelayDialogAction
    object SaveDelayType: DelayDialogAction
    object DismissDelayType: DelayDialogAction

    data class SetCustomTime(val time: LocalDateTime): DelayDialogAction
}