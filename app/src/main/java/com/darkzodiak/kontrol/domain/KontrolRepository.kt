package com.darkzodiak.kontrol.domain

import com.darkzodiak.kontrol.data.local.entity.App
import com.darkzodiak.kontrol.domain.model.Profile
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

    fun getAllApps(): Flow<List<App>>
    suspend fun getAppById(id: Long): App
}