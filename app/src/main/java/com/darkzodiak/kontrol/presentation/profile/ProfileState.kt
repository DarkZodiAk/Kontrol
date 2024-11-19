package com.darkzodiak.kontrol.presentation.profile

import com.darkzodiak.kontrol.data.local.entity.App

data class ProfileState(
    val isNewProfile: Boolean = true,
    val name: String = "",
    val apps: List<App> = emptyList(),
    val selectedApps: List<App> = emptyList(),
    val warning: String? = null,
)
