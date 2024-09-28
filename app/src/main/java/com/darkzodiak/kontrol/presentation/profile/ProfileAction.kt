package com.darkzodiak.kontrol.presentation.profile

sealed interface ProfileAction {
    object Back: ProfileAction
    object Done: ProfileAction
    data class ModifyName(val text: String): ProfileAction
    data class SelectApp(val appName: String): ProfileAction
    data class UnselectApp(val appName: String): ProfileAction
}