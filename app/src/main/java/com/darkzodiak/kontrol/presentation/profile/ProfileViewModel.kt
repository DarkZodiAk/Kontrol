package com.darkzodiak.kontrol.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.data.local.entity.App
import com.darkzodiak.kontrol.data.local.entity.Profile
import com.darkzodiak.kontrol.domain.KontrolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: KontrolRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(ProfileState())
        private set

    private var profile = Profile()
    private var profileApps = emptyList<App>()

    init {
        repository.getAllApps()
            .onEach { apps ->
                state = state.copy(apps = apps)
            }.launchIn(viewModelScope)

        savedStateHandle.get<Long>("id")?.let { id ->
            viewModelScope.launch {
                profile = repository.getProfileById(id)
                profileApps = repository.getProfileAppsById(id).first()
                state = state.copy(
                    name = profile.name,
                    selectedApps = profileApps
                )
            }
        }
    }

    fun onAction(action: ProfileAction) {
        when(action) {
            ProfileAction.Done -> {
                if(state.name.isBlank() && state.selectedApps.isEmpty()) {
                    return
                }
                viewModelScope.launch {
                    var id = profile.id
                    if(id != null) {
                        repository.updateProfile(profile.copy(name = state.name))
                    } else {
                        id = repository.addProfile(profile.copy(name = state.name))
                    }
                    state.selectedApps.forEach { app ->
                        repository.addAppToProfile(profileId = id, appId = app.id!!)
                    }
                    (profileApps - state.selectedApps).forEach { app ->
                        repository.deleteAppFromProfile(profileId = id, appId = app.id!!)
                    }
                }
            }
            is ProfileAction.ModifyName -> {
                state = state.copy(name = action.text)
            }
            is ProfileAction.SelectApp -> {
                state = state.copy(
                    selectedApps = state.selectedApps + action.app
                )
            }
            is ProfileAction.UnselectApp -> {
                state = state.copy(
                    selectedApps = state.selectedApps - action.app
                )
            }
            else -> Unit
        }
    }
}