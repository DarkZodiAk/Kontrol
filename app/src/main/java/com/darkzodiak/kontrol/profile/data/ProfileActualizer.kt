package com.darkzodiak.kontrol.profile.data

import android.content.Context
import com.darkzodiak.kontrol.core.data.FirstLaunchPostBootDetector
import com.darkzodiak.kontrol.core.data.millisUntil
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.data.local.ProfileStateType
import com.darkzodiak.kontrol.profile.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.scheduling.EventCache
import com.darkzodiak.kontrol.scheduling.EventScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileActualizer @Inject constructor(
    @ApplicationContext context: Context,
    private val profileDao: ProfileDao,
    private val eventScheduler: EventScheduler
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var firstCallAfterBoot = FirstLaunchPostBootDetector(context).isFirstLaunch()

    init {
        EventCache.profileActualizer = this
    }

    fun actualize(profileId: Long) = scope.launch {
        val profile = profileDao.getProfileById(profileId)
        var newProfile = profile.copy()

        newProfile.pausedUntil?.let { resumeTime ->
            if (millisUntil(resumeTime) <= 0) {
                newProfile = newProfile.copy(
                    state = ProfileStateType.ACTIVE,
                    pausedUntil = null
                )
            }
        }

        if (newProfile.editRestrictionType == EditRestrictionType.UNTIL_DATE) {
            val untilDate = newProfile.eRestrictUntilDate
            val stopAfterDate = newProfile.eStopAfterReachingUntilDate
            if (untilDate != null && stopAfterDate != null) {
                if (millisUntil(untilDate) <= 0) {
                    newProfile = newProfile.copy(
                        state = if (stopAfterDate) ProfileStateType.STOPPED else newProfile.state,
                        editRestrictionType = EditRestrictionType.NO_RESTRICTION,
                        eRestrictUntilDate = null,
                        eStopAfterReachingUntilDate = null
                    )
                }
            }
        }

        if (newProfile.editRestrictionType == EditRestrictionType.UNTIL_REBOOT
            && newProfile.state == ProfileStateType.ACTIVE && firstCallAfterBoot) {
            val stopAfterReboot = newProfile.eStopAfterReboot
            if (stopAfterReboot != null) {
                newProfile = newProfile.copy(
                    state = if (stopAfterReboot) ProfileStateType.STOPPED else ProfileStateType.ACTIVE,
                    editRestrictionType = EditRestrictionType.NO_RESTRICTION,
                    eStopAfterReboot = null
                )
            }
        }

        if (newProfile != profile) {
            profileDao.updateProfile(newProfile)
            eventScheduler.updateEvent(newProfile.id ?: return@launch)
        }
    }

    fun actualizeAll() = scope.launch {
        val profiles = profileDao.getProfiles().first()
        val jobs = profiles.mapNotNull { profile ->
            if (profile.id != null) actualize(profile.id) else null
        }
        jobs.joinAll()
        firstCallAfterBoot = false
    }
}