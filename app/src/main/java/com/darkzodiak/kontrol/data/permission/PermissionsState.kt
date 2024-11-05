package com.darkzodiak.kontrol.data.permission

data class PermissionsState(
    val hasUsageStatsPermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val hasAlertWindowPermission: Boolean = false,
    val hasAllPermissions: Boolean = false
)
