package com.darkzodiak.kontrol.profile.presentation.appRestriction

import com.darkzodiak.kontrol.core.data.local.types.AppRestrictionType

sealed interface AppRestrictionAction {
    object Dismiss: AppRestrictionAction
    object Save: AppRestrictionAction

    data class SetRestriction(val type: AppRestrictionType): AppRestrictionAction

    object DismissDialog: AppRestrictionAction
    data class SendDialogData(val data: DialogData): AppRestrictionAction
}

sealed interface DialogData {
    data class Password(val text: String): DialogData
    data class RandomText(val length: Int): DialogData
}