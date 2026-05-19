package com.darkzodiak.kontrol.permission

data class PermissionsState(
    val hasUsageStatsPermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val hasAlertWindowPermission: Boolean = false
) {
    val hasEssentialPermissions: Boolean
        get() = hasAccessibilityPermission && hasAlertWindowPermission
    val hasAllPermissions: Boolean
        get() = hasUsageStatsPermission && hasAccessibilityPermission && hasAlertWindowPermission
}
