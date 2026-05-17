package com.darkzodiak.kontrol.profile.data

import com.darkzodiak.kontrol.apps.data.AppMapper
import com.darkzodiak.kontrol.apps.domain.App
import com.darkzodiak.kontrol.apps.domain.AppRepository
import com.darkzodiak.kontrol.core.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.core.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.profile.data.scheduling.EventScheduler
import com.darkzodiak.kontrol.profile.domain.ProfileRepository
import com.darkzodiak.kontrol.profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val appRepository: AppRepository,
    private val eventScheduler: EventScheduler,
    private val profileActualizer: ProfileActualizer
): ProfileRepository {
    override suspend fun addProfile(profile: Profile): Long {
        val id = profileDao.insertProfile(ProfileMapper.profileToProfileEntity(profile))
        eventScheduler.upsertEvent(id)
        return id
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(ProfileMapper.profileToProfileEntity(profile))
        if (profile.id == Profile.DEFAULT_ID) return
        eventScheduler.upsertEvent(profile.id)
    }

    override suspend fun deleteProfile(profile: Profile) {
        profileDao.deleteProfileById(profile.id)
        if (profile.id == Profile.DEFAULT_ID) return
        eventScheduler.deleteEvent(profile.id)
    }

    override fun getProfiles(): Flow<List<Profile>> {
        return profileDao.getProfiles().map {
            it.map { ProfileMapper.profileEntityToProfile(it) }
        }
    }

    override suspend fun getProfileById(id: Long): Profile? {
        val profileEntity = profileDao.getProfileById(id) ?: return null
        return ProfileMapper.profileEntityToProfile(profileEntity)
    }

    override suspend fun addAppToProfile(appId: Long, profileId: Long) {
        if (appRepository.getAppById(appId) == null) return
        profileDao.addAppToProfile(AppToProfile(profileId, appId))
    }

    override suspend fun deleteAppFromProfile(appId: Long, profileId: Long) {
        profileDao.deleteAppFromProfile(AppToProfile(profileId, appId))
        val app = appRepository.getAppById(appId)
        if (app?.isDeleted == true && profileDao.isAppInProfiles(appId).not()) {
            appRepository.deleteAppById(appId)
        }
    }

    override fun getProfileAppsById(profileId: Long): Flow<List<App>> {
        return profileDao.getProfileAppsById(profileId).map {
            AppMapper.entityListToDomainList(it)
        }
    }

    override suspend fun getProfilesWithApp(packageName: String): Flow<List<Profile>> {
        val appId = appRepository.getAppByPackageName(packageName)?.id ?: return flowOf(emptyList())

        return profileDao.getProfilesWithApp(appId).map {
            it.map { ProfileMapper.profileEntityToProfile(it) }
        }
    }

    override fun actualizeAllProfiles() {
        profileActualizer.actualizeAll()
    }
}