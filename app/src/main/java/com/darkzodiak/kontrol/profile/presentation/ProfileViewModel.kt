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
) : ViewModel() {

    private val timeSource = TimeSource()
    private val interScreenCache = ProfileInterScreenBus.get()

    var state by mutableStateOf(ProfileScreenState())
        private set

    private val channel = Channel<ProfileEvent>()
    val events = channel.receiveAsFlow()

    private var profile = Profile()
    private var profileApps = emptyList<App>()

    init {
        savedStateHandle.get<Long>("id")?.let { id ->
            viewModelScope.launch {
                profile = repository.getProfileById(id)
                profileApps = repository.getProfileAppsById(id).first()
                state = state.copy(
                    name = profile.name,
                    apps = profileApps,
                    editRestriction = profile.editRestriction,
                )
            }
            state = state.copy(isNewProfile = false)
        }

        timeSource.currentTime.onEach { time ->
            checkTimedRestriction(time)
        }.launchIn(viewModelScope)

        interScreenCache.appList.onEach {
            state = state.copy(apps = it)
        }.launchIn(viewModelScope)

        interScreenCache.editRestriction.onEach {
            state = state.copy(editRestriction = it)
        }.launchIn(viewModelScope)

        // TODO(): Uncomment on implementing AppRestrictions
//        interScreenCache.appRestriction.onEach {
//
//        }.launchIn(viewModelScope)
    }

    fun onAction(action: ProfileAction) {
        when(action) {
            ProfileAction.Done -> {
                profileUploader.uploadProfile(profile, profileApps, state)
            }
            is ProfileAction.ModifyName -> {
                state = state.copy(name = action.text)
            }
            ProfileAction.OpenEditRestriction -> {
                interScreenCache.sendEditRestriction(state.editRestriction)
            }
            ProfileAction.OpenAppsList -> {
                interScreenCache.sendAppList(state.apps)
            }
            else -> Unit
        }
    }

    override fun onCleared() {
        ProfileInterScreenBus.clear()
        super.onCleared()
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
}