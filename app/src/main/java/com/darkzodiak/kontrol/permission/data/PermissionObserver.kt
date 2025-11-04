package com.darkzodiak.kontrol.permission.data

import android.content.Context
import com.darkzodiak.kontrol.permission.hasAccessibilityPermission
import com.darkzodiak.kontrol.permission.hasAlertWindowPermission
import com.darkzodiak.kontrol.permission.hasUsageStatsPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val applicationScope: CoroutineScope
) {

    private val _permissionState = MutableStateFlow(PermissionsState())
    val permissionsState = _permissionState.asStateFlow()

    val canRunService = permissionsState
        .map { it.hasEssentialPermissions }
        .stateIn(applicationScope, SharingStarted.Lazily, false)


    fun updateAllPermissions() {
        updateUsageStatsPermission()
        updateAccessibilityPermission()
        updateAlertWindowPermission()
    }

    fun updateUsageStatsPermission() {
        applicationScope.launch {
            val enabled = context.hasUsageStatsPermission()
            _permissionState.update {
                it.copy(
                    hasUsageStatsPermission = enabled,
                    hasEssentialPermissions = it.hasAccessibilityPermission && it.hasAlertWindowPermission
                )
            }
        }
    }

    fun updateAccessibilityPermission() {
        applicationScope.launch {
            val enabled = context.hasAccessibilityPermission()
            _permissionState.update {
                it.copy(
                    hasAccessibilityPermission = enabled,
                    hasEssentialPermissions = enabled && it.hasAlertWindowPermission
                )
            }
        }
    }

    fun updateAlertWindowPermission() {
        applicationScope.launch {
            val enabled = context.hasAlertWindowPermission()
            _permissionState.update {
                it.copy(
                    hasAlertWindowPermission = enabled,
                    hasEssentialPermissions = it.hasAccessibilityPermission && enabled
                )
            }
        }
    }
}