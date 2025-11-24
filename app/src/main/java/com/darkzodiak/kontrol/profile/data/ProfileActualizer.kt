package com.darkzodiak.kontrol.profile.data

import com.darkzodiak.kontrol.core.data.millisUntil
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.data.local.ProfileStateType
import com.darkzodiak.kontrol.profile.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.scheduling.EventCache
import com.darkzodiak.kontrol.scheduling.EventScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileActualizer @Inject constructor(
    private val profileDao: ProfileDao,
    private val eventScheduler: EventScheduler
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        EventCache.profileActualizer = this
    }

    fun actualize(profileId: Long) = scope.launch {
        val profile = profileDao.getProfileById(profileId)
        var newProfile = profile.copy()

        profile.pausedUntil?.let { resumeTime ->
            if (millisUntil(resumeTime) <= 0) {
                newProfile = newProfile.copy(
                    state = ProfileStateType.ACTIVE,
                    pausedUntil = null
                )
            }
        }

        if (profile.editRestrictionType == EditRestrictionType.UNTIL_DATE) {
            val untilDate = profile.restrictUntilDate
            val unlockAfterDate = profile.unlockAfterReachingUntilDate
            if (untilDate != null && unlockAfterDate != null) {
                if (millisUntil(untilDate) <= 0) {
                    newProfile = newProfile.copy(
                        state = if (unlockAfterDate) ProfileStateType.STOPPED else ProfileStateType.ACTIVE,
                        editRestrictionType = EditRestrictionType.NO_RESTRICTION,
                        restrictUntilDate = null,
                        unlockAfterReachingUntilDate = null
                    )
                }
            }
        }

        if (newProfile != profile) {
            profileDao.updateProfile(newProfile)
            eventScheduler.updateEvent(newProfile.id ?: return@launch)
        }
    }

    fun actualizeAll() = scope.launch {
        val profiles = profileDao.getProfiles().first()
        profiles.forEach { profile ->
            actualize(profile.id ?: return@forEach)
        }
    }
}