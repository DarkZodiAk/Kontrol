package com.darkzodiak.kontrol.profile.presentation.appList

import com.darkzodiak.kontrol.core.data.local.entity.App

data class AppListState(
    val apps: List<App> = emptyList(),
    val selectedApps: List<App> = emptyList(),
    val unsaved: Boolean = false
)
