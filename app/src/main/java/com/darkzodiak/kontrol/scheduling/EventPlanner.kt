package com.darkzodiak.kontrol.scheduling

import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.data.local.dao.ProfileDao
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventPlanner @Inject constructor(
    private val profileDao: ProfileDao
) {

    suspend fun getNearestEventTime(profileId: Long): LocalDateTime? {
        val profile = profileDao.getProfileById(profileId) ?: return null

        return if (profile.pausedUntil != null) profile.pausedUntil
        else if (profile.editRestrictionType == EditRestrictionType.UNTIL_DATE) profile.eRestrictUntilDate
        else null
    }
}