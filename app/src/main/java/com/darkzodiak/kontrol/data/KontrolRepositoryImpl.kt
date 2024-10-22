package com.darkzodiak.kontrol.data

import com.darkzodiak.kontrol.data.local.dao.AppDao
import com.darkzodiak.kontrol.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.data.local.entity.App
import com.darkzodiak.kontrol.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.data.local.entity.Profile
import com.darkzodiak.kontrol.domain.KontrolRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class KontrolRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val appDao: AppDao
): KontrolRepository {
    override suspend fun addProfile(profile: Profile): Long {
        return profileDao.insertProfile(profile)
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(profile)
    }

    override suspend fun deleteProfile(profile: Profile) {
        profileDao.deleteProfile(profile)
    }

    override fun getProfiles(): Flow<List<Profile>> {
        return profileDao.getProfiles()
    }

    override suspend fun getProfileById(id: Long): Profile {
        return profileDao.getProfileById(id)
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
            return profileDao.isAppInProfiles(id)
        }
        return false
    }


    override suspend fun addApp(app: App) {
        appDao.insertApp(app)
    }

    override suspend fun deleteApp(app: App) {
        appDao.deleteApp(app)
    }

    override fun getAllApps(): Flow<List<App>> {
        return appDao.getAllApps()
    }

    override suspend fun getAppById(id: Long): App {
        return appDao.getAppById(id)
    }
}