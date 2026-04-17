package com.darkzodiak.kontrol.profile.domain

import com.darkzodiak.kontrol.core.domain.App
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.profile.domain.model.Profile
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProfileOverlapChecker @Inject constructor(
    private val repository: KontrolRepository
) {
    suspend fun isProfileOverlappedByOthers(profile: Profile, profileApps: List<App>): Boolean {
        val profileAppIds = profileApps.map { it.id }.toSet()
        val otherProfiles = repository.getProfiles().first().filter { it.id != profile.id }
        otherProfiles.forEach { otherProfile ->
            val apps = repository.getProfileAppsById(otherProfile.id).first()
            if (apps.any { it.id in profileAppIds }) return true
        }
        return false
    }
}