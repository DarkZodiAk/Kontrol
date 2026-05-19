package com.darkzodiak.kontrol.permission.data

import android.app.AppOpsManager
import android.content.Context
import android.content.Context.APP_OPS_SERVICE
import android.os.Build
import android.os.Process
import android.provider.Settings

object PermissionChecker {
    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val packageName = context.packageName
        val mode = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        } else {
            appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun hasAccessibilityPermission(context: Context): Boolean {
        val packageName = context.packageName
        val accessibilityServiceName = "$packageName/$packageName.monitor_block.KontrolService"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(accessibilityServiceName) == true
    }

    fun hasAlertWindowPermission(context: Context): Boolean {
        return if(Build.VERSION.SDK_INT < 23) true
        else Settings.canDrawOverlays(context)
    }
}