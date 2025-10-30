package com.darkzodiak.kontrol.home

import com.darkzodiak.kontrol.home.profileCard.ProfileCardIntent
import com.darkzodiak.kontrol.permission.domain.Permission
import com.darkzodiak.kontrol.profile.domain.Profile

sealed interface HomeAction {
    object NewProfile: HomeAction

    data class RequestProfileAction(val profile: Profile, val intent: ProfileCardIntent): HomeAction

    object RestrictionPassed: HomeAction
    object RestrictionNotPassed: HomeAction

    data class UpdatePermissionInfo(val permission: Permission): HomeAction
}