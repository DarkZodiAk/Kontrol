package com.darkzodiak.kontrol.presentation.home

import com.darkzodiak.kontrol.domain.Permission
import com.darkzodiak.kontrol.domain.model.EditRestriction
import com.darkzodiak.kontrol.domain.model.Profile

sealed interface HomeAction {
    object None: HomeAction
    object NewProfile: HomeAction
    data class OpenProfile(val id: Long): HomeAction
    data class DeleteProfile(val profile: Profile): HomeAction
    data class SwitchProfileState(val profile: Profile): HomeAction
    data class UpdatePermissionInfo(val permission: Permission): HomeAction

    data class PrepareForUnlock(
        val action: HomeAction,
        val restriction: EditRestriction
    ): HomeAction
}