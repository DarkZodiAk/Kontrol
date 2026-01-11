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
import com.darkzodiak.kontrol.profile.domain.ProfileUpdater
import com.darkzodiak.kontrol.profile.domain.EditRestriction
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
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

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
            }
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

    fun onAction(action: ProfileAction) {
        when(action) {
            ProfileAction.Done -> {
                profileUpdater.updateProfileWithApps(
                    profile = createRelevantProfileInstance(),
                    profileApps = state.apps
                )
                viewModelScope.launch { channel.send(ProfileEvent.GoBack) }
            }
            is ProfileAction.ModifyName -> {
                state = state.copy(name = action.text, unsaved = true)
            }
            ProfileAction.OpenAppsList -> {
                ignoreBus = true
                interScreenBus.sendAppList(state.apps)
            }
            ProfileAction.OpenAppRestriction -> {
                ignoreBus = true
                interScreenBus.sendAppRestriction(state.appRestriction)
            }
            ProfileAction.OpenEditRestriction -> {
                ignoreBus = true
                interScreenBus.sendEditRestriction(state.editRestriction)
            }
            else -> Unit
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
            viewModelScope.launch {
                channel.send(ProfileEvent.ShowWarning(
                    text = "Блокировка профиля достигла отмеченной даты и была отключена"
                ))
            }
        }
    }

    override fun onCleared() {
        ProfileInterScreenBus.clear()
        super.onCleared()
    }
}