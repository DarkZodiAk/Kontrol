package com.darkzodiak.kontrol.core.data

import com.darkzodiak.kontrol.core.data.local.dao.AppDao
import com.darkzodiak.kontrol.profile.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.profile.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.profile.data.ProfileMapper
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.profile.domain.Profile
import com.darkzodiak.kontrol.scheduling.EventScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class KontrolRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val appDao: AppDao,
    private val eventScheduler: EventScheduler
): KontrolRepository {
    override suspend fun addProfile(profile: Profile): Long {
        val id = profileDao.insertProfile(ProfileMapper.profileToProfileEntity(profile))
        eventScheduler.addEvent(id)
        return id
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(ProfileMapper.profileToProfileEntity(profile))
        eventScheduler.updateEvent(profile.id ?: return)
    }

    override suspend fun deleteProfile(profile: Profile) {
        profileDao.deleteProfileById(profile.id ?: return)
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
        profileDao.addAppToProfile(AppToProfile(profileId, appId))
    }

    override suspend fun deleteAppFromProfile(appId: Long, profileId: Long) {
        profileDao.deleteAppFromProfile(AppToProfile(profileId, appId))
    }

    override fun getProfileAppsById(profileId: Long): Flow<List<App>> {
        return profileDao.getProfileAppsById(profileId)
    }

    override suspend fun getProfilesWithApp(packageName: String): Flow<List<Profile>>? {
        val appId = appDao.getAppByPackageName(packageName)?.id ?: return null

        return profileDao.getProfilesWithApp(appId).map {
            it.map { ProfileMapper.profileEntityToProfile(it) }
        }
    }

    override fun getAllApps(): Flow<List<App>> {
        return appDao.getAllApps()
    }

    override suspend fun getAppById(id: Long): App? {
        return appDao.getAppById(id)
    }

    override suspend fun getAppByPackageName(packageName: String): App? {
        return appDao.getAppByPackageName(packageName)
    }
}