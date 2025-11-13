package com.darkzodiak.kontrol.scheduling

import com.darkzodiak.kontrol.profile.data.local.dao.ProfileDao
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventPlanner @Inject constructor(
    private val profileDao: ProfileDao
) {

    suspend fun getNearestEventTime(profileId: Long): LocalDateTime? {
        val profile = profileDao.getProfileById(profileId)

        return profile.pausedUntil
    }
}