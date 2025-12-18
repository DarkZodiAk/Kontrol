package com.darkzodiak.kontrol.profile.presentation.editRestriction

import com.darkzodiak.kontrol.profile.domain.EditRestriction

data class EditRestrictionState(
    val restriction: EditRestriction = EditRestriction.NoRestriction,
    val openedDialogType: DialogType = DialogType.NONE,
    val unsaved: Boolean = false
)
