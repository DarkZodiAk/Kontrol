package com.darkzodiak.kontrol.home

import com.darkzodiak.kontrol.permission.domain.Permission
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.Profile

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