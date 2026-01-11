package com.darkzodiak.kontrol.profile.presentation.appList

sealed interface AppListEvent {
    object GoBack: AppListEvent
}