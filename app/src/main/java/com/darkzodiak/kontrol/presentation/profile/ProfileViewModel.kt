package com.darkzodiak.kontrol.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.data.local.entity.App
import com.darkzodiak.kontrol.domain.KontrolRepository
import com.darkzodiak.kontrol.domain.model.Profile
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
                    selectedApps = profileApps,
                    selectedUnsaved = profileApps,
                    editRestriction = profile.editRestriction,
                    editRestrictionUnsaved = profile.editRestriction
                )
            }
            state = state.copy(isNewProfile = false)
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
                        repository.updateProfile(profile.copy(name = state.name, editRestriction = state.editRestriction))
                    } else {
                        id = repository.addProfile(profile.copy(name = state.name, editRestriction = state.editRestriction))
                    }

                    state.selectedApps.forEach { app ->
                        repository.addAppToProfile(profileId = id, appId = app.id!!)
                    }
                    (profileApps.map { it.id } - state.selectedApps.map { it.id }).forEach { appId ->
                        repository.deleteAppFromProfile(profileId = id, appId = appId!!)
                    }

                }
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
}