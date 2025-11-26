package com.darkzodiak.kontrol.profile.presentation.appRestriction

import com.darkzodiak.kontrol.profile.domain.AppRestriction

data class AppRestrictionState(
    val restriction: AppRestriction = AppRestriction.SimpleBlock,
    val openedDialogType: DialogType = DialogType.NONE,
//    val infoText: String? = null
)