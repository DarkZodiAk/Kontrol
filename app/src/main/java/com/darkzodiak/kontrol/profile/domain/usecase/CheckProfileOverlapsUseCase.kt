package com.darkzodiak.kontrol.profile.domain.usecase

import com.darkzodiak.kontrol.apps.domain.App
import com.darkzodiak.kontrol.profile.domain.ProfileRepository
import com.darkzodiak.kontrol.profile.domain.model.Profile
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckProfileOverlapsUseCase @Inject constructor(
    private val repository: ProfileRepository
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