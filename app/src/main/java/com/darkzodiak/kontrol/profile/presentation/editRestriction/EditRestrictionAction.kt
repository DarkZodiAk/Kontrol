package com.darkzodiak.kontrol.profile.presentation.editRestriction

import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import java.time.LocalDateTime

sealed interface EditRestrictionAction {
    object Dismiss: EditRestrictionAction
    object Save: EditRestrictionAction

    data class SetRestriction(val type: EditRestrictionType): EditRestrictionAction

    object DismissDialog: EditRestrictionAction
    data class SendDialogData(val data: DialogData): EditRestrictionAction
    data class SwitchOption(val option: OptionType): EditRestrictionAction
}

enum class OptionType {
    STOP_AFTER_DATE, STOP_AFTER_REBOOT
}

sealed interface DialogData {
    data class Password(val text: String): DialogData
    data class RandomText(val length: Int): DialogData
    data class UntilDate(val date: LocalDateTime): DialogData
}