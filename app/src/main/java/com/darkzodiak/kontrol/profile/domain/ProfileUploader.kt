package com.darkzodiak.kontrol.profile.domain

import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.profile.presentation.ProfileScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileUploader @Inject constructor(
    private val repository: KontrolRepository,
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    // TODO(): Make signature decoupled from ProfileViewModel
    fun uploadProfile(profile: Profile, profileApps: List<App>, state: ProfileScreenState) {
        if(state.name.isBlank() && state.apps.isEmpty()) {
            return
        }
        scope.launch {
            var id = profile.id
            if(id != null) {
                repository.updateProfile(profile.copy(
                    name = state.name,
                    appRestriction = state.appRestriction,
                    editRestriction = state.editRestriction
                ))
            } else {
                id = repository.addProfile(profile.copy(
                    name = state.name,
                    appRestriction = state.appRestriction,
                    editRestriction = state.editRestriction
                ))
            }

            state.apps.forEach { app ->
                repository.addAppToProfile(profileId = id, appId = app.id!!)
            }
            (profileApps.map { it.id } - state.apps.map { it.id }).forEach { appId ->
                repository.deleteAppFromProfile(profileId = id, appId = appId!!)
            }
        }
    }
}