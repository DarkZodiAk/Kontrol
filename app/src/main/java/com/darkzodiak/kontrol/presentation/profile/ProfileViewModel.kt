package com.darkzodiak.kontrol.presentation.profile

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.data.local.entity.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileDao: ProfileDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(ProfileState())
        private set

    private var profile = Profile()
    private var profileApps = emptyList<String>()

    init {
        val apps = getAllInstalledApps()
        state = state.copy(
            apps = apps
        )
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
                    state.selectedApps.forEach { appName ->
                        profileDao.addAppToProfile(
                            AppToProfile(profileId = id, appPackageName = appName)
                        )
                    }
                    (profileApps - state.selectedApps).forEach { appName ->
                        profileDao.deleteAppFromProfile(
                            AppToProfile(profileId = id, appPackageName = appName)
                        )
                    }
                }
            }
            is ProfileAction.ModifyName -> {
                state = state.copy(name = action.text)
            }
            is ProfileAction.SelectApp -> {
                state = state.copy(
                    selectedApps = state.selectedApps + action.appName
                )
            }
            is ProfileAction.UnselectApp -> {
                state = state.copy(
                    selectedApps = state.selectedApps - action.appName
                )
            }
            else -> Unit
        }
    }

    private fun getAllInstalledApps(): List<String> {
        val packageManager: PackageManager = context.packageManager

        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        return apps
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .map { it.loadLabel(packageManager).toString() }
    }
}