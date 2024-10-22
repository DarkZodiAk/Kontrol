package com.darkzodiak.kontrol.presentation.permission

data class PermissionState(
    val hasUsageStatsPermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val hasAllPermissions: Boolean = false
)
