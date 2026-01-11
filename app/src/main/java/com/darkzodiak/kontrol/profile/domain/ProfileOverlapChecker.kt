package com.darkzodiak.kontrol.profile.domain

import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileOverlapChecker @Inject constructor(
    private val repository: KontrolRepository
) {
    suspend fun isProfileOverlappedByOthers(profile: Profile, profileApps: List<App>): Boolean {
        val profileAppIds = profileApps.map { it.id }.toSet()
        val otherProfiles = repository.getProfiles().first().filter { it.id != profile.id }
        otherProfiles.forEach { otherProfile ->
            if (otherProfile.id == null) return@forEach
            val apps = repository.getProfileAppsById(otherProfile.id).first()
            if (apps.any { it.id in profileAppIds }) return true
        }
        return false
    }
}