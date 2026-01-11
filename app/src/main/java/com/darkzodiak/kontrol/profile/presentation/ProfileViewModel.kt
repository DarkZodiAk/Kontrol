package com.darkzodiak.kontrol.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.profile.domain.Profile
import com.darkzodiak.kontrol.core.presentation.time.TimeSource
import com.darkzodiak.kontrol.core.presentation.warning.WarningType
import com.darkzodiak.kontrol.profile.domain.ProfileUpdater
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.ProfileOverlapChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: KontrolRepository,
    private val profileUpdater: ProfileUpdater,
    private val profileOverlapChecker: ProfileOverlapChecker,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var rendered = false
    private val timeSource = TimeSource()
    private val interScreenBus = ProfileInterScreenBus.get()
    private var ignoreBus = false

    var state by mutableStateOf(ProfileScreenState())
        private set

    private val channel = Channel<ProfileEvent>()
    val events = channel.receiveAsFlow()

    private var profile = Profile()
    private var profileApps = emptyList<App>()

    init {
        savedStateHandle.get<Long>("id")?.let { id ->
            viewModelScope.launch {
                val profileFromDb = repository.getProfileById(id)
                if (profileFromDb == null) {
                    state = state.copy(isNewProfile = true)
                    return@launch
                }
                profile = profileFromDb
                profileApps = repository.getProfileAppsById(id).first()
                state = state.copy(
                    name = profile.name,
                    apps = profileApps,
                    appRestriction = profile.appRestriction,
                    editRestriction = profile.editRestriction,
                    isNewProfile = false
                )
                checkProfileAppsOverlap()
            }
        }
        savedStateHandle.get<Boolean>("protectedMode")?.let {
            state = state.copy(
                protectedMode = it,
                warnings = if (it) state.warnings + WarningType.LOCKED_EDIT else state.warnings
            )
        }

        timeSource.currentTime.onEach { time ->
            checkTimedRestriction(time)
        }.launchIn(viewModelScope)

        interScreenBus.appList.onEach {
            if (ignoreBus) {
                ignoreBus = false
                return@onEach
            }
            val unsaved = state.apps != it
            state = state.copy(apps = it, unsaved = unsaved)
            checkProfileAppsOverlap()
        }.launchIn(viewModelScope)

        interScreenBus.editRestriction.onEach {
            if (ignoreBus) {
                ignoreBus = false
                return@onEach
            }
            val unsaved = state.editRestriction != it
            state = state.copy(editRestriction = it, unsaved = unsaved)
        }.launchIn(viewModelScope)

        interScreenBus.appRestriction.onEach {
            if (ignoreBus) {
                ignoreBus = false
                return@onEach
            }
            val unsaved = state.appRestriction != it
            state = state.copy(appRestriction = it, unsaved = unsaved)
        }.launchIn(viewModelScope)
    }

    fun render() {
        rendered = true
    }

    fun onAction(action: ProfileAction) {
        if (rendered.not()) return
        when(action) {
            ProfileAction.GoBack -> {
                rendered = false
                sendEvent(ProfileEvent.GoBack)
            }
            ProfileAction.Done -> {
                rendered = false
                profileUpdater.updateProfileWithApps(
                    profile = createRelevantProfileInstance(),
                    profileApps = state.apps
                )
                sendEvent(ProfileEvent.GoBack)
            }
            is ProfileAction.ModifyName -> {
                state = state.copy(name = action.text, unsaved = true)
            }
            ProfileAction.OpenAppsList -> {
                ignoreBus = true
                interScreenBus.sendAppList(state.apps)
                sendEvent(ProfileEvent.OpenAppsList)
            }
            ProfileAction.OpenAppRestriction -> {
                ignoreBus = true
                interScreenBus.sendAppRestriction(state.appRestriction)
                sendEvent(ProfileEvent.OpenAppRestriction)
            }
            ProfileAction.OpenEditRestriction -> {
                ignoreBus = true
                interScreenBus.sendEditRestriction(state.editRestriction)
                sendEvent(ProfileEvent.OpenEditRestriction)
            }
        }
    }

    private fun createRelevantProfileInstance(): Profile {
        return profile.copy(
            name = state.name,
            appRestriction = state.appRestriction,
            editRestriction = state.editRestriction
        )
    }

    private fun checkTimedRestriction(currentTime: LocalDateTime) {
        val editRestriction = state.editRestriction as? EditRestriction.UntilDate

        if (editRestriction != null && editRestriction.date <= currentTime) {
            state = state.copy(editRestriction = EditRestriction.NoRestriction)
            sendEvent(ProfileEvent.ShowWarning(
                text = "Блокировка профиля достигла отмеченной даты и была отключена"
            ))
        }
    }

    private suspend fun checkProfileAppsOverlap() {
        val overlapped = profileOverlapChecker.isProfileOverlappedByOthers(
            profile = profile,
            profileApps = state.apps
        )
        if (overlapped) {
            state = state.copy(warnings = state.warnings + WarningType.PROFILE_OVERLAP)
        } else {
            state = state.copy(warnings = state.warnings - WarningType.PROFILE_OVERLAP)
        }
    }

    private fun sendEvent(event: ProfileEvent) {
        viewModelScope.launch {
            channel.send(event)
        }
    }

    override fun onCleared() {
        ProfileInterScreenBus.clear()
        super.onCleared()
    }
}