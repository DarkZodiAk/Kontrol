package com.darkzodiak.kontrol.profile.presentation

sealed interface ProfileEvent {
    data class ShowWarning(val text: String): ProfileEvent
    object GoBack: ProfileEvent
    object OpenAppsList: ProfileEvent
    object OpenAppRestriction: ProfileEvent
    object OpenEditRestriction: ProfileEvent
}