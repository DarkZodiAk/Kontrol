package com.darkzodiak.kontrol.domain

import com.darkzodiak.kontrol.data.local.entity.Profile
import kotlinx.coroutines.flow.Flow

interface KontrolRepository {
    suspend fun addProfile(profile: Profile): Long
    suspend fun updateProfile(profile: Profile)
    suspend fun deleteProfile(profile: Profile)
    fun getProfiles(): Flow<List<Profile>>
    suspend fun getProfileById(id: Long): Profile
    suspend fun addAppToProfile(appId: Long, profileId: Long)
    suspend fun deleteAppFromProfile(appId: Long, profileId: Long)
    fun getProfileAppsById(id: Long): Flow<List<App>>

    suspend fun isAppInProfiles(packageName: String): Boolean

    suspend fun addApp(app: App)
    suspend fun deleteApp(app: App)
    fun getAllApps(): Flow<List<App>>
    suspend fun getAppById(id: Long): App
}