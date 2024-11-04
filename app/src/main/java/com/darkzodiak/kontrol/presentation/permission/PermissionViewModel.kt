package com.darkzodiak.kontrol.presentation.permission

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.darkzodiak.kontrol.data.AppObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val appObserver: AppObserver
): ViewModel() {
    var state by mutableStateOf(PermissionState())
        private set

    fun onAction(action: PermissionAction) {
        when(action) {
            PermissionAction.GoToApp -> {
                appObserver.update()
            }
            is PermissionAction.SendPermissionInfo -> {
                when(action.permission) {
                    Permission.USAGE_STATS_ACCESS -> {
                        state = state.copy(hasUsageStatsPermission = action.hasPermission)
                        updatePermissionState()
                    }

                    Permission.ACCESSIBILITY -> {
                        state = state.copy(hasAccessibilityPermission = action.hasPermission)
                        updatePermissionState()
                    }

                    Permission.SYSTEM_ALERT_WINDOW -> {
                        state = state.copy(hasAlertWindowPermission = action.hasPermission)
                        updatePermissionState()
                    }
                }
            }
        }
    }

    private fun updatePermissionState() {
        state = state.copy(
            hasAllPermissions = state.hasUsageStatsPermission &&
                    state.hasAccessibilityPermission &&
                    state.hasAlertWindowPermission
        )
    }
}