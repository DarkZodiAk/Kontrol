package com.darkzodiak.kontrol.profile.domain

import com.darkzodiak.kontrol.apps.domain.App
import com.darkzodiak.kontrol.profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun actualizeAllProfiles()
    suspend fun addProfile(profile: Profile): Long
    suspend fun updateProfile(profile: Profile)
    suspend fun deleteProfile(profile: Profile)
    fun getProfiles(): Flow<List<Profile>>
    suspend fun getProfileById(id: Long): Profile?
    suspend fun addAppToProfile(appId: Long, profileId: Long)
    suspend fun deleteAppFromProfile(appId: Long, profileId: Long)
    fun getProfileAppsById(profileId: Long): Flow<List<App>>
    suspend fun getProfilesWithApp(packageName: String): Flow<List<Profile>>
    suspend fun isAppInProfiles(appId: Long): Boolean
}