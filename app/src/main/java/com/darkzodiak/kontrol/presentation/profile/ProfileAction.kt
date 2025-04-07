package com.darkzodiak.kontrol.presentation.profile

import com.darkzodiak.kontrol.data.local.entity.App
import com.darkzodiak.kontrol.domain.model.EditRestriction


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