package com.darkzodiak.kontrol.core.presentation.delayDialog

import java.time.LocalDateTime

sealed interface DelayDialogAction {
    object Close: DelayDialogAction

    object OpenTimePicker: DelayDialogAction
    object DismissTimePicker: DelayDialogAction

    object OpenDatePicker: DelayDialogAction
    object DismissDatePicker: DelayDialogAction

    object OpenDelayTypePicker: DelayDialogAction
    data class SelectDelayType(val type: DelayType): DelayDialogAction
    object SaveDelayType: DelayDialogAction
    object DismissDelayTypeDialog: DelayDialogAction

    data class SaveDateTime(val time: LocalDateTime): DelayDialogAction
}