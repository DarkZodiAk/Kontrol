package com.darkzodiak.kontrol.home

import com.darkzodiak.kontrol.home.profileCard.ProfileCardIntent
import com.darkzodiak.kontrol.permission.domain.Permission
import com.darkzodiak.kontrol.profile.domain.Profile
import java.time.LocalDateTime

sealed interface HomeAction {
    object OpenPermissionSheet: HomeAction
    object DismissPermissionSheet: HomeAction

    object NewProfile: HomeAction
    object OpenLockedProfile: HomeAction

    data class RequestProfileAction(val profile: Profile, val intent: ProfileCardIntent): HomeAction

    object RestrictionPassed: HomeAction
    object RestrictionNotPassed: HomeAction

    sealed interface Delay: HomeAction {
        object OpenDialog: Delay
        data class Save(val delayUntil: LocalDateTime): Delay
        object Dismiss: Delay
    }

    data class UpdatePermissionInfo(val permission: Permission): HomeAction
}