package com.darkzodiak.kontrol.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.domain.KontrolRepository
import com.darkzodiak.kontrol.domain.Permission
import com.darkzodiak.kontrol.domain.eventBus.PermissionEvent
import com.darkzodiak.kontrol.domain.eventBus.PermissionEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: KontrolRepository,
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    init {
        repository.getProfiles().onEach {
            state = state.copy(profiles = it)
        }.launchIn(viewModelScope)

        PermissionEventBus.permissionBus.onEach { event ->
            when(event) {
                PermissionEvent.LostAccessibilityPermission -> {
                    state = state.copy(hasAccessibilityPermission = false)
                    updatePermissionState()
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: HomeAction) {
        when(action) {
            is HomeAction.SwitchProfileState -> {
                viewModelScope.launch {
                    repository.updateProfile(action.profile)
                }
            }
            is HomeAction.SendPermissionInfo -> {
                when(action.permission) {
                    Permission.USAGE_STATS_ACCESS -> {
                        state = state.copy(hasUsageStatsPermission = action.granted)
                        updatePermissionState()
                    }

                    Permission.ACCESSIBILITY -> {
                        state = state.copy(hasAccessibilityPermission = action.granted)
                        updatePermissionState()
                    }

                    Permission.SYSTEM_ALERT_WINDOW -> {
                        state = state.copy(hasAlertWindowPermission = action.granted)
                        updatePermissionState()
                    }
                }
            }
            else -> Unit
        }
    }


    private fun updatePermissionState() {
        state = state.copy(
            hasAllPermissions = state.hasUsageStatsPermission &&
                    state.hasAccessibilityPermission &&
                    state.hasAlertWindowPermission
        )
        if(state.hasAllPermissions) {
            viewModelScope.launch {
                PermissionEventBus.sendEvent(PermissionEvent.GrantedAllPermissions)
            }
        }
    }
}