package com.darkzodiak.kontrol.profile.domain

import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileUpdater @Inject constructor(
    private val repository: KontrolRepository,
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun updateProfileWithApps(profile: Profile, profileApps: List<App>) {
        scope.launch {
            val profileId = updateProfile(profile)
            updateProfileApps(profileId, profileApps)
        }
    }

    private suspend fun updateProfile(profile: Profile): Long {
        var id = profile.id
        if(id != null) {
            repository.updateProfile(profile)
        } else {
            id = repository.addProfile(profile)
        }
        return id
    }

    private suspend fun updateProfileApps(profileId: Long, apps: List<App>) {
        if (apps.isEmpty()) return

        val currentApps = repository.getProfileAppsById(profileId).first()
        apps.forEach { app ->
            repository.addAppToProfile(profileId = profileId, appId = app.id!!)
        }
        (currentApps.map { it.id } - apps.map { it.id }).forEach { appId ->
            repository.deleteAppFromProfile(profileId = profileId, appId = appId!!)
        }
    }
}