package com.darkzodiak.kontrol.data

import com.darkzodiak.kontrol.data.local.dao.AppDao
import com.darkzodiak.kontrol.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.data.local.entity.App
import com.darkzodiak.kontrol.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.data.mappers.ProfileMapper
import com.darkzodiak.kontrol.domain.KontrolRepository
import com.darkzodiak.kontrol.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class KontrolRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val appDao: AppDao
): KontrolRepository {
    override suspend fun addProfile(profile: Profile): Long {
        return profileDao.insertProfile(ProfileMapper.profileToProfileEntity(profile))
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(ProfileMapper.profileToProfileEntity(profile))
    }

    override suspend fun deleteProfile(profile: Profile) {
        profileDao.deleteProfile(ProfileMapper.profileToProfileEntity(profile))
    }

    override fun getProfiles(): Flow<List<Profile>> {
        return profileDao.getProfiles().map {
            it.map { ProfileMapper.profileEntityToProfile(it) }
        }
    }

    override suspend fun getProfileById(id: Long): Profile {
        return ProfileMapper.profileEntityToProfile(profileDao.getProfileById(id))
    }

    override suspend fun addAppToProfile(appId: Long, profileId: Long) {
        profileDao.addAppToProfile(AppToProfile(profileId, appId))
    }

    override suspend fun deleteAppFromProfile(appId: Long, profileId: Long) {
        profileDao.deleteAppFromProfile(AppToProfile(profileId, appId))
    }

    override fun getProfileAppsById(id: Long): Flow<List<App>> {
        return profileDao.getProfileAppsById(id)
    }


    override suspend fun isAppInProfiles(packageName: String): Boolean {
        appDao.getAppByPackageName(packageName)?.id?.let { id ->
            return profileDao.getProfilesByApp(id).any {
                it.isEnabled
            }
        }
        return false
    }

    override fun getAllApps(): Flow<List<App>> {
        return appDao.getAllApps()
    }

    override suspend fun getAppById(id: Long): App {
        return appDao.getAppById(id)
    }
}