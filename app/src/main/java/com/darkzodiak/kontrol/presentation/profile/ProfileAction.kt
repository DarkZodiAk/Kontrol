package com.darkzodiak.kontrol.presentation.profile

import com.darkzodiak.kontrol.data.local.entity.App

sealed interface ProfileAction {
    object Back: ProfileAction
    object Done: ProfileAction
    data class ModifyName(val text: String): ProfileAction
    data class SelectApp(val app: App): ProfileAction
    data class UnselectApp(val app: App): ProfileAction
}