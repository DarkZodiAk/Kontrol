package com.darkzodiak.kontrol.permission.data

data class PermissionsState(
    val hasUsageStatsPermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val hasAlertWindowPermission: Boolean = false,
    val hasEssentialPermissions: Boolean = false
)
