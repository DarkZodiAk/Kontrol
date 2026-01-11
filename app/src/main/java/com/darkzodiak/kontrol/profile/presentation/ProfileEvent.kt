package com.darkzodiak.kontrol.profile.presentation

sealed interface ProfileEvent {
    data class ShowWarning(val text: String): ProfileEvent
    object GoBack: ProfileEvent
}