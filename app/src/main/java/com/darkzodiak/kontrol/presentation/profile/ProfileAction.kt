package com.darkzodiak.kontrol.presentation.profile

import com.darkzodiak.kontrol.domain.App


sealed interface ProfileAction {
    object Back: ProfileAction
    object Done: ProfileAction
    data class ModifyName(val text: String): ProfileAction
    object OpenAppsList: ProfileAction

    sealed interface Apps: ProfileAction {
        data class SelectApp(val app: App): Apps
        data class UnselectApp(val app: App): Apps
        object Dismiss: Apps
        object Save: Apps
    }
}