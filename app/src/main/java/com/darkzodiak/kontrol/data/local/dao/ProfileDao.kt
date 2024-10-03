package com.darkzodiak.kontrol.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.darkzodiak.kontrol.data.local.entity.App
import com.darkzodiak.kontrol.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.data.local.entity.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert
    suspend fun insertProfile(profile: Profile): Long

    @Update
    suspend fun updateProfile(profile: Profile)

    @Delete
    suspend fun deleteProfile(profile: Profile)

    @Query("SELECT * FROM profile")
    fun getProfiles(): Flow<List<Profile>>

    @Query("SELECT * FROM profile WHERE id = :id")
    fun getProfileById(id: Long): Flow<Profile>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAppToProfile(appToProfile: AppToProfile)

    @Delete
    suspend fun deleteAppFromProfile(appToProfile: AppToProfile)

    @Query("SELECT * FROM app WHERE id IN (SELECT appId FROM apptoprofile WHERE profileId = :id)")
    fun getProfileAppsById(id: Long): Flow<List<App>>
}