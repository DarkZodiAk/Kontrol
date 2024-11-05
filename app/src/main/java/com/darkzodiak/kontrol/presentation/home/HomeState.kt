package com.darkzodiak.kontrol.presentation.home

import com.darkzodiak.kontrol.data.local.entity.Profile

data class HomeState(
    val profiles: List<Profile> = emptyList(),
    val hasUsageStatsPermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val hasAlertWindowPermission: Boolean = false,
    val hasAllPermissions: Boolean = false
)
