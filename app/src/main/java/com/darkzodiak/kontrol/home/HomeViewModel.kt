package com.darkzodiak.kontrol.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.permission.data.PermissionObserver
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.core.presentation.time.TimeSource
import com.darkzodiak.kontrol.home.profileCard.PendingProfileIntent
import com.darkzodiak.kontrol.home.profileCard.ProfileCardIntent
import com.darkzodiak.kontrol.permission.domain.Permission
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.Profile
import com.darkzodiak.kontrol.profile.domain.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: KontrolRepository,
    private val permissionObserver: PermissionObserver
) : ViewModel() {

    private val timeSource = TimeSource()

    private var pendingCardIntent: PendingProfileIntent? = null
    private var pendingDelayProfile: Profile? = null

    var state by mutableStateOf(HomeScreenState())
        private set

    private val channel = Channel<HomeEvent>()
    val events = channel.receiveAsFlow()

    init {
        repository.getProfiles().onEach {
            state = state.copy(profiles = it)
        }.launchIn(viewModelScope)

        permissionObserver.permissionsState.onEach {
            state = state.copy(permissions = state.permissions.copy(
                hasAccessibilityPermission = it.hasAccessibilityPermission,
                hasAlertWindowPermission = it.hasAlertWindowPermission,
                hasEssentialPermissions = it.hasEssentialPermissions
            ))
        }.launchIn(viewModelScope)

        timeSource.currentTime.onEach { time ->
            state = state.copy(curTime = time)
        }.launchIn(viewModelScope)
    }

    fun onAction(action: HomeAction) {
        when(action) {
            is HomeAction.RequestProfileAction -> {
                val (profile, intent) = action
                if (profile.state is ProfileState.Active) {
                    pendingCardIntent = PendingProfileIntent(profile, intent)
                    state = state.copy(
                        curRestriction = profile.editRestriction,
                        restrictionDialogVisible = true
                    )
                    return
                }
                executeProfileIntent()
            }
            HomeAction.RestrictionPassed -> {
                closeRestrictionDialog()
                executeProfileIntent()
            }
            HomeAction.RestrictionNotPassed -> {
                // TODO(): Create ability to open profile if check was failed
                closeRestrictionDialog()
            }
            is HomeAction.UpdatePermissionInfo -> {
                when(action.permission) {
                    Permission.ACCESSIBILITY -> {
                        permissionObserver.updateAccessibilityPermission()
                    }
                    Permission.SYSTEM_ALERT_WINDOW -> {
                        permissionObserver.updateAlertWindowPermission()
                    }
                    else -> Unit
                }
            }

            HomeAction.Delay.OpenPause -> {
                state = state.copy(pauseDialogVisible = true, activateAfterDialogVisible = false)
            }
            HomeAction.Delay.OpenActivateAfter -> {
                state = state.copy(pauseDialogVisible = false, activateAfterDialogVisible = true)
            }
            is HomeAction.Delay.Save -> {
                pendingDelayProfile?.let { profile ->
                    val newProfile = profile.copy(state = ProfileState.Paused(action.delayUntil))
                    viewModelScope.launch { repository.updateProfile(newProfile) }
                }
                pendingDelayProfile = null
            }
            HomeAction.Delay.Dismiss -> {
                pendingDelayProfile = null
            }

            else -> Unit
        }
    }

    private fun closeRestrictionDialog() {
        state = state.copy(
            curRestriction = EditRestriction.NoRestriction,
            restrictionDialogVisible = false
        )
    }

    private fun executeProfileIntent() {
        pendingCardIntent?.run {
            when (intent) {
                ProfileCardIntent.ACTIVATE -> {
                    viewModelScope.launch {
                        repository.updateProfile(profile.copy(state = ProfileState.Active))
                    }
                }
                ProfileCardIntent.DELAYED_ACTIVATE -> {
                    pendingDelayProfile = profile
                    onAction(HomeAction.Delay.OpenActivateAfter)
                }
                ProfileCardIntent.STOP -> {
                    viewModelScope.launch {
                        repository.updateProfile(profile.copy(state = ProfileState.Stopped))
                    }
                }
                ProfileCardIntent.PAUSE -> {
                    pendingDelayProfile = profile
                    onAction(HomeAction.Delay.OpenPause)
                }
                ProfileCardIntent.DELETE -> {
                    viewModelScope.launch {
                        repository.deleteProfile(profile)
                    }
                }
                ProfileCardIntent.OPEN -> {
                    viewModelScope.launch {
                        if (profile.id == null) channel.send(HomeEvent.ShowError("Profile open error"))
                        else channel.send(HomeEvent.OpenProfile(profile.id))
                    }
                }
            }
        }
        pendingCardIntent = null
    }
}