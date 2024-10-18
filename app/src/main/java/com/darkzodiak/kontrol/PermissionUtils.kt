package com.darkzodiak.kontrol

import android.app.AppOpsManager
import android.content.Context
import android.content.Context.APP_OPS_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings

fun Context.hasUsageStatisticsPermission(): Boolean {
    val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
    val mode = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
    } else {
        appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

fun Context.getUsageStatsIntent(): Intent {
    return Intent(
        Settings.ACTION_USAGE_ACCESS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
}

fun Context.getAccessibilityIntent(): Intent {
    return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
}

fun Context.hasAccessibilityPermission(): Boolean {
    val accessibilityServiceName = "$packageName/$packageName.data.KontrolService"
    val enabledServices = Settings.Secure.getString(
        contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    return enabledServices?.contains(accessibilityServiceName) == true
}
