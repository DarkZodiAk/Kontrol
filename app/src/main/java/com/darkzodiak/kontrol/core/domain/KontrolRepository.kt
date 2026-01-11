package com.darkzodiak.kontrol.core.domain

import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.profile.domain.Profile
import kotlinx.coroutines.flow.Flow

interface KontrolRepository {
    suspend fun addProfile(profile: Profile): Long
    suspend fun updateProfile(profile: Profile)
    suspend fun deleteProfile(profile: Profile)
    fun getProfiles(): Flow<List<Profile>>
    suspend fun getProfileById(id: Long): Profile?
    suspend fun addAppToProfile(appId: Long, profileId: Long)
    suspend fun deleteAppFromProfile(appId: Long, profileId: Long)
    fun getProfileAppsById(profileId: Long): Flow<List<App>>
    suspend fun getProfilesWithApp(packageName: String): Flow<List<Profile>>?
    fun getAllApps(): Flow<List<App>>
    suspend fun getAppById(id: Long): App?
    suspend fun getAppByPackageName(packageName: String): App?
}