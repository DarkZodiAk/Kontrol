package com.darkzodiak.kontrol.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.darkzodiak.kontrol.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.data.local.entity.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Upsert
    suspend fun upsertProfile(profile: Profile)

    @Delete
    suspend fun deleteProfile(profile: Profile)

    @Query("SELECT * FROM profile")
    fun getProfiles(): Flow<List<Profile>>

    @Insert
    suspend fun addAppToProfile(appToProfile: AppToProfile)

    @Delete
    suspend fun deleteAppFromProfile(appToProfile: AppToProfile)

    @Query("SELECT * FROM apptoprofile WHERE profileId = :id")
    fun getProfileAppsById(id: Long): Flow<List<String>>
}