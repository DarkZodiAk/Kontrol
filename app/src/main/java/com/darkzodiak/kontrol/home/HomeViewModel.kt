package com.darkzodiak.kontrol.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.core.presentation.time.TimeSource
import com.darkzodiak.kontrol.home.profileCard.PendingProfileIntent
import com.darkzodiak.kontrol.home.profileCard.ProfileCardIntent
import com.darkzodiak.kontrol.permission.data.PermissionObserver
import com.darkzodiak.kontrol.permission.domain.Permission
import com.darkzodiak.kontrol.profile.domain.model.EditRestriction
import com.darkzodiak.kontrol.profile.domain.model.Profile
import com.darkzodiak.kontrol.profile.domain.model.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: KontrolRepository,
    private val permissionObserver: PermissionObserver,
) : ViewModel() {

    private var rendered = false
    private val timeSource = TimeSource()

    private var pendingCardIntent: PendingProfileIntent? = null
    private var pendingDelayProfile: Profile? = null
    private var viewLockedProfileSnackbarPresent = false

    var state by mutableStateOf(HomeScreenState())
        private set

    private val channel = Channel<HomeEvent>()
    val events = channel.receiveAsFlow()

    init {
        repository.getProfiles().onEach {
            state = state.copy(profiles = it)
        }.launchIn(viewModelScope)

        permissionObserver.permissionsState.onEach {
            state = state.copy(
                permissions = HomeScreenState.Permissions(
                    hasAccessibilityPermission = it.hasAccessibilityPermission,
                    hasAlertWindowPermission = it.hasAlertWindowPermission,
                    hasUsageStatsPermission = it.hasUsageStatsPermission,
                    hasAllPermissions = it.hasAllPermissions,
                ),
                permissionSheetVisible = state.permissionSheetVisible && it.hasAllPermissions.not()
            )
        }.launchIn(viewModelScope)

        timeSource.currentTime.onEach { time ->
            state = state.copy(curTime = time)
        }.launchIn(viewModelScope)
    }

    fun onAction(action: HomeAction) {
        if (rendered.not()) return
        when(action) {
            is HomeAction.RequestProfileAction -> {
                closeDialogOrSheet()
                processProfileActionRequest(action.profile, action.intent)
            }
            HomeAction.RestrictionPassed -> {
                closeDialogOrSheet()
                executeProfileIntent()
            }
            HomeAction.RestrictionNotPassed -> {
                closeDialogOrSheet()
                processNotPassedRestriction()
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

            HomeAction.Delay.OpenDialog -> {
                closeDialogOrSheet()
                state = state.copy(
                    delayDialogVisible = true,
                    oldPauseDate = (pendingDelayProfile?.state as? ProfileState.Paused)?.until
                )
            }
            is HomeAction.Delay.Save -> {
                pendingDelayProfile?.let { profile ->
                    val newProfile = profile.copy(state = ProfileState.Paused(action.delayUntil))
                    viewModelScope.launch { repository.updateProfile(newProfile) }
                }
                closeDialogOrSheet()
                pendingDelayProfile = null
            }
            HomeAction.Delay.Dismiss -> {
                closeDialogOrSheet()
                pendingDelayProfile = null
            }

            HomeAction.NewProfile -> {
                closeDialogOrSheet()
                unrender()
                sendEvent(HomeEvent.NewProfile)
            }
            HomeAction.ViewLockedProfile -> {
                closeDialogOrSheet()
                val profileId = pendingCardIntent?.profile?.id
                pendingCardIntent = null
                if (profileId == null) {
                    sendEvent(HomeEvent.ShowError("Profile open error"))
                } else {
                    unrender()
                    sendEvent(HomeEvent.OpenProfile(profileId, true))
                }
            }
            HomeAction.DismissViewLockedProfile -> {
                viewLockedProfileSnackbarPresent = false
            }
            HomeAction.OpenPermissionSheet -> {
                closeDialogOrSheet()
                state = state.copy(permissionSheetVisible = true)
            }
            HomeAction.DismissPermissionSheet -> {
                closeDialogOrSheet()
            }
        }
    }

    fun render() {
        rendered = true
    }

    private fun unrender() {
        rendered = false
        viewLockedProfileSnackbarPresent = false
    }

    private fun processNotPassedRestriction() {
        if (pendingCardIntent?.intent == ProfileCardIntent.OPEN) {
            if (viewLockedProfileSnackbarPresent.not()) {
                viewLockedProfileSnackbarPresent = true
                sendEvent(HomeEvent.OfferViewProfileInProtectedMode)
            }
        } else {
            sendEvent(HomeEvent.ProfileIntentBlocked)
        }
        pendingCardIntent = null
    }

    private fun closeDialogOrSheet() {
        state = state.copy(
            permissionSheetVisible = false,
            curRestriction = EditRestriction.NoRestriction,
            restrictionDialogVisible = false,
            delayDialogVisible = false,
            oldPauseDate = null
        )
    }

    private fun processProfileActionRequest(profile: Profile, intent: ProfileCardIntent) {
        pendingCardIntent = PendingProfileIntent(profile, intent)
        val restriction = profile.editRestriction
        if (profile.state is ProfileState.Active && restriction !is EditRestriction.NoRestriction) {

            if (restriction.isOneOf(softRestrictions)) {
                state = state.copy(
                    curRestriction = restriction,
                    restrictionDialogVisible = true
                )
            } else if (restriction.isOneOf(hardRestrictions)) {
                processNotPassedRestriction()
            }
            return
        }
        executeProfileIntent()
    }

    private fun executeProfileIntent() {
        pendingCardIntent?.run {
            when (intent) {
                ProfileCardIntent.ACTIVATE -> {
                    viewModelScope.launch {
                        repository.updateProfile(profile.copy(state = ProfileState.Active))
                    }
                }
                ProfileCardIntent.STOP -> {
                    viewModelScope.launch {
                        repository.updateProfile(profile.copy(state = ProfileState.Stopped))
                    }
                }
                ProfileCardIntent.OPEN -> {
                    unrender()
                    sendEvent(HomeEvent.OpenProfile(profile.id))
                }
                ProfileCardIntent.DELETE -> {
                    viewModelScope.launch {
                        repository.deleteProfile(profile)
                    }
                }
                ProfileCardIntent.DELAYED_ACTIVATE -> {
                    openDelayDialog(profile)
                }
                ProfileCardIntent.PAUSE -> {
                    openDelayDialog(profile)
                }
                ProfileCardIntent.CHANGE_PAUSE -> {
                    openDelayDialog(profile)
                }
            }
        }
        pendingCardIntent = null
    }

    private fun openDelayDialog(profile: Profile) {
        pendingDelayProfile = profile
        onAction(HomeAction.Delay.OpenDialog)
    }

    private fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            channel.send(event)
        }
    }

    companion object {
        private val softRestrictions = setOf(
            EditRestriction.Password::class,
            EditRestriction.RandomText::class
        )
        private val hardRestrictions = setOf(
            EditRestriction.UntilDate::class,
            EditRestriction.UntilReboot::class
        )

        private fun EditRestriction.isOneOf(types: Set<KClass<out EditRestriction>>): Boolean {
            return types.any { it.isInstance(this) }
        }
    }
}