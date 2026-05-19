package com.darkzodiak.kontrol.permission

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
        val enabled = PermissionChecker.hasUsageStatsPermission(context)
        updateState { copy(hasUsageStatsPermission = enabled) }
    }

    fun updateAccessibilityPermission() {
        val enabled = PermissionChecker.hasAccessibilityPermission(context)
        updateState { copy(hasAccessibilityPermission = enabled) }
    }

    fun updateAlertWindowPermission() {
        val enabled = PermissionChecker.hasAlertWindowPermission(context)
        updateState { copy(hasAlertWindowPermission = enabled) }
    }

    private fun updateState(update: PermissionsState.() -> PermissionsState) {
        _permissionState.update { update(_permissionState.value) }
    }
}