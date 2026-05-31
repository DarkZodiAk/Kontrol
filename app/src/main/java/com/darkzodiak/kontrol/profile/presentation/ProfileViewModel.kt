package com.darkzodiak.kontrol.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.apps.domain.App
import com.darkzodiak.kontrol.apps.domain.AppRepository
import com.darkzodiak.kontrol.core.presentation.time.TimeSource
import com.darkzodiak.kontrol.core.presentation.warning.WarningType
import com.darkzodiak.kontrol.profile.domain.ProfileRepository
import com.darkzodiak.kontrol.profile.domain.ProfileUpdater
import com.darkzodiak.kontrol.profile.domain.model.EditRestriction
import com.darkzodiak.kontrol.profile.domain.model.Profile
import com.darkzodiak.kontrol.profile.domain.usecase.CheckProfileOverlapsUseCase
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
    private val profileRepository: ProfileRepository,
    private val appRepository: AppRepository,
    private val profileUpdater: ProfileUpdater,
    private val checkProfileOverlapsUseCase: CheckProfileOverlapsUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var rendered = false
    private val timeSource = TimeSource()
    private val interScreenMediator = ProfileInterScreenMediator.get()

    var state by mutableStateOf(ProfileScreenState())
        private set

    private val channel = Channel<ProfileEvent>()
    val events = channel.receiveAsFlow()

    private var profile = Profile()
    private var profileApps = emptyList<App>()

    init {
        savedStateHandle.get<Long>("id")?.let { id ->
            viewModelScope.launch {
                val profileFromDb = profileRepository.getProfileById(id)
                if (profileFromDb == null) {
                    state = state.copy(isNewProfile = true)
                    return@launch
                }
                profile = profileFromDb
                profileApps = profileRepository.getProfileAppsById(id).first()
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

        interScreenMediator.appList.onEach {
            val unsaved = state.apps != it
            state = state.copy(apps = it, unsaved = unsaved)
            checkProfileAppsOverlap()
        }.launchIn(viewModelScope)

        interScreenMediator.editRestriction.onEach {
            val unsaved = state.editRestriction != it
            state = state.copy(editRestriction = it, unsaved = unsaved)
        }.launchIn(viewModelScope)

        interScreenMediator.appRestriction.onEach {
            val unsaved = state.appRestriction != it
            state = state.copy(appRestriction = it, unsaved = unsaved)
        }.launchIn(viewModelScope)

        appRepository.getAllApps().onEach { apps ->
            state.apps.onEach { profileApp ->
                val app = apps.find { it.id == profileApp.id }
                if (app == null) {
                    state = state.copy(apps = state.apps - profileApp)
                } else if (profileApp.isDeleted != app.isDeleted) {
                    state = state.copy(apps = state.apps - profileApp + app)
                }
            }
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
                rendered = false
                viewModelScope.launch { interScreenMediator.sendAppList(state.apps) }
                sendEvent(ProfileEvent.OpenAppsList)
            }
            ProfileAction.OpenAppRestriction -> {
                rendered = false
                viewModelScope.launch { interScreenMediator.sendAppRestriction(state.appRestriction) }
                sendEvent(ProfileEvent.OpenAppRestriction)
            }
            ProfileAction.OpenEditRestriction -> {
                rendered = false
                viewModelScope.launch { interScreenMediator.sendEditRestriction(state.editRestriction) }
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
        val overlapped = checkProfileOverlapsUseCase.isProfileOverlappedByOthers(
            profile = profile,
            profileApps = state.apps
        )
        if (overlapped) {
            state = state.copy(warnings = (state.warnings.toSet() + WarningType.PROFILE_OVERLAP).toList())
        } else {
            state = state.copy(warnings = (state.warnings.toSet() - WarningType.PROFILE_OVERLAP).toList())
        }
    }

    private fun sendEvent(event: ProfileEvent) {
        viewModelScope.launch {
            channel.send(event)
        }
    }

    override fun onCleared() {
        ProfileInterScreenMediator.clear()
        super.onCleared()
    }
}