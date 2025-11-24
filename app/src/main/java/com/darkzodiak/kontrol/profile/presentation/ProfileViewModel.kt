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
import com.darkzodiak.kontrol.core.domain.usecase.GetAllAppsUseCase
import com.darkzodiak.kontrol.core.presentation.time.TimeSource
import com.darkzodiak.kontrol.profile.domain.ProfileUploader
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
    private val profileUploader: ProfileUploader,
    savedStateHandle: SavedStateHandle,
    getAllAppsUseCase: GetAllAppsUseCase
) : ViewModel() {

    private val timeSource = TimeSource()

    var state by mutableStateOf(ProfileScreenState())
        private set

    private val channel = Channel<ProfileEvent>()
    val events = channel.receiveAsFlow()

    private var profile = Profile()
    private var profileApps = emptyList<App>()

    init {
        getAllAppsUseCase().onEach { apps ->
            state = state.copy(apps = apps)
        }.launchIn(viewModelScope)

        savedStateHandle.get<Long>("id")?.let { id ->
            viewModelScope.launch {
                profile = repository.getProfileById(id)
                profileApps = repository.getProfileAppsById(id).first()
                state = state.copy(
                    name = profile.name,
                    selectedApps = profileApps,
                    selectedUnsaved = profileApps,
                    editRestriction = profile.editRestriction,
                    editRestrictionUnsaved = profile.editRestriction
                )
            }
            state = state.copy(isNewProfile = false)
        }

        timeSource.currentTime.onEach { time ->
            checkTimedRestriction(time)
        }.launchIn(viewModelScope)
    }

    fun onAction(action: ProfileAction) {
        when(action) {
            ProfileAction.Done -> {
                profileUploader.uploadProfile(profile, profileApps, state)
            }
            is ProfileAction.ModifyName -> {
                state = state.copy(name = action.text)
            }

            is ProfileAction.Apps.SelectApp -> {
                state = state.copy(
                    selectedUnsaved = state.selectedUnsaved + action.app
                )
            }
            is ProfileAction.Apps.UnselectApp -> {
                state = state.copy(
                    selectedUnsaved = state.selectedUnsaved.filter { it.id != action.app.id }
                )
            }
            ProfileAction.Apps.Dismiss -> {
                state = state.copy(selectedUnsaved = state.selectedApps)
            }
            ProfileAction.Apps.Save -> {
                state = state.copy(selectedApps = state.selectedUnsaved)
            }

            is ProfileAction.Restriction.UpdateType -> {
                state = state.copy(editRestrictionUnsaved = action.type)
            }
            ProfileAction.Restriction.Dismiss -> {
                state = state.copy(editRestrictionUnsaved = state.editRestriction)
            }
            ProfileAction.Restriction.Save -> {
                state = state.copy(editRestriction = state.editRestrictionUnsaved)
            }

            else -> Unit
        }
    }

    private fun checkTimedRestriction(currentTime: LocalDateTime) {
        val editRestriction = state.editRestriction as? EditRestriction.UntilDate
        val editRestrictionUnsaved = state.editRestrictionUnsaved as? EditRestriction.UntilDate

        if (editRestriction == null && editRestrictionUnsaved == null) return

        if (editRestriction != null && editRestriction.date <= currentTime) {
            state = state.copy(editRestriction = EditRestriction.NoRestriction)
            viewModelScope.launch {
                channel.send(ProfileEvent.ShowWarning(
                    text = "Блокировка профиля достигла отмеченной даты и была отключена"
                ))
            }
        }
        if (editRestrictionUnsaved != null && editRestrictionUnsaved.date <= currentTime) {
            state = state.copy(editRestrictionUnsaved = EditRestriction.NoRestriction)
            viewModelScope.launch {
                channel.send(ProfileEvent.ShowWarningOnRestriction(
                    text = "Блокировка профиля достигла отмеченной даты и была отключена"
                ))
            }
        }
    }
}