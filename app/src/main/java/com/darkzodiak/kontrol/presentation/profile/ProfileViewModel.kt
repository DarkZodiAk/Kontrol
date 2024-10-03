package com.darkzodiak.kontrol.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.data.local.dao.AppDao
import com.darkzodiak.kontrol.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.data.local.entity.App
import com.darkzodiak.kontrol.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.data.local.entity.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileDao: ProfileDao,
    private val appDao: AppDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(ProfileState())
        private set

    private var profile = Profile()
    private var profileApps = emptyList<App>()

    init {
        appDao.getAllApps()
            .onEach { apps ->
                state = state.copy(apps = apps)
            }.launchIn(viewModelScope)

        savedStateHandle.get<Long>("id")?.let { id ->
            viewModelScope.launch {
                profile = profileDao.getProfileById(id).first()
                profileApps = profileDao.getProfileAppsById(id).first()
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
                        profileDao.updateProfile(profile.copy(name = state.name))
                    } else {
                        id = profileDao.insertProfile(profile.copy(name = state.name))
                    }
                    state.selectedApps.forEach { app ->
                        profileDao.addAppToProfile(
                            AppToProfile(profileId = id, appId = app.id!!)
                        )
                    }
                    (profileApps - state.selectedApps).forEach { app ->
                        profileDao.deleteAppFromProfile(
                            AppToProfile(profileId = id, appId = app.id!!)
                        )
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