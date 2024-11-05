package com.darkzodiak.kontrol.presentation.home

import com.darkzodiak.kontrol.data.local.entity.Profile
import com.darkzodiak.kontrol.domain.Permission

sealed interface HomeAction {
    object NewProfile: HomeAction
    data class OpenProfile(val id: Long): HomeAction
    data class SwitchProfileState(val profile: Profile): HomeAction
    data class SendPermissionInfo(val permission: Permission, val granted: Boolean): HomeAction
}