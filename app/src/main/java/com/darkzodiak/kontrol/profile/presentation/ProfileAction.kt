package com.darkzodiak.kontrol.profile.presentation

sealed interface ProfileAction {
    object Back: ProfileAction
    object Done: ProfileAction
    data class ModifyName(val text: String): ProfileAction
    object OpenAppsList: ProfileAction
    object OpenAppRestriction: ProfileAction
    object OpenEditRestriction: ProfileAction
}