package com.darkzodiak.kontrol.profile.presentation.appRestriction

sealed interface AppRestrictionEvent {
    object GoBack: AppRestrictionEvent
}