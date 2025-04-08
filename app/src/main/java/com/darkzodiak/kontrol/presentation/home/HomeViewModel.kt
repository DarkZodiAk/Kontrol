package com.darkzodiak.kontrol.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.data.permission.PermissionObserver
import com.darkzodiak.kontrol.domain.KontrolRepository
import com.darkzodiak.kontrol.domain.Permission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: KontrolRepository,
    private val permissionObserver: PermissionObserver
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    init {
        repository.getProfiles().onEach {
            state = state.copy(profiles = it)
        }.launchIn(viewModelScope)

        permissionObserver.permissionsState.onEach {
            state = state.copy(
                hasUsageStatsPermission = it.hasUsageStatsPermission,
                hasAccessibilityPermission = it.hasAccessibilityPermission,
                hasAlertWindowPermission = it.hasAlertWindowPermission,
                hasAllPermissions = it.hasAllPermissions
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(action: HomeAction) {
        when(action) {
            is HomeAction.SwitchProfileState -> {
                viewModelScope.launch {
                    repository.updateProfile(action.profile)
                }
            }
            is HomeAction.DeleteProfile -> {
                viewModelScope.launch {
                    repository.deleteProfile(action.profile)
                }
            }
            is HomeAction.UpdatePermissionInfo -> {
                when(action.permission) {
                    Permission.USAGE_STATS_ACCESS -> {
                        permissionObserver.updateUsageStatsPermission()
                    }
                    Permission.ACCESSIBILITY -> {
                        permissionObserver.updateAccessibilityPermission()
                    }
                    Permission.SYSTEM_ALERT_WINDOW -> {
                        permissionObserver.updateAlertWindowPermission()
                    }
                }
            }
            is HomeAction.PrepareForUnlock -> {
                state = state.copy(
                    pendingAction = action.action,
                    curRestriction = action.restriction
                )
            }
            else -> Unit
        }
    }
}