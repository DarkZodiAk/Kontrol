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
            updateState { copy(hasUsageStatsPermission = enabled) }
            updateCompositeFieldsInState()
        }
    }

    fun updateAccessibilityPermission() {
        applicationScope.launch {
            val enabled = context.hasAccessibilityPermission()
            updateState { copy(hasAccessibilityPermission = enabled) }
            updateCompositeFieldsInState()
        }
    }

    fun updateAlertWindowPermission() {
        applicationScope.launch {
            val enabled = context.hasAlertWindowPermission()
            updateState { copy(hasAlertWindowPermission = enabled) }
            updateCompositeFieldsInState()
        }
    }

    private fun updateCompositeFieldsInState() {
        updateState { copy(
            hasEssentialPermissions = hasAccessibilityPermission && hasAlertWindowPermission,
            hasAllPermissions = hasAccessibilityPermission && hasAlertWindowPermission && hasUsageStatsPermission
        ) }
    }

    private fun updateState(update: PermissionsState.() -> PermissionsState) {
        _permissionState.update { update(_permissionState.value) }
    }
}