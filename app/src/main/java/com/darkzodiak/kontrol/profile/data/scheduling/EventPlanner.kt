package com.darkzodiak.kontrol.profile.data.scheduling

import com.darkzodiak.kontrol.core.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.core.data.local.types.EditRestrictionType
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
        else if (profile.editRestrictionType == EditRestrictionType.UNTIL_DATE) profile.eUntilDate
        else null
    }
}