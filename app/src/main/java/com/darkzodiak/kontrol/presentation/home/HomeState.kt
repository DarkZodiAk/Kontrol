package com.darkzodiak.kontrol.presentation.home

import com.darkzodiak.kontrol.domain.model.Profile

data class HomeState(
    val profiles: List<Profile> = emptyList(),
    val hasUsageStatsPermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val hasAlertWindowPermission: Boolean = false,
    val hasAllPermissions: Boolean = false
)
