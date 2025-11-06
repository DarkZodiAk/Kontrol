package com.darkzodiak.kontrol.profile.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.profile.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.profile.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert
    suspend fun insertProfile(profileEntity: ProfileEntity): Long

    @Update
    suspend fun updateProfile(profileEntity: ProfileEntity)

    @Delete
    suspend fun deleteProfile(profileEntity: ProfileEntity)

    @Query("SELECT * FROM profileentity")
    fun getProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profileentity WHERE id = :id")
    suspend fun getProfileById(id: Long): ProfileEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAppToProfile(appToProfile: AppToProfile)

    @Delete
    suspend fun deleteAppFromProfile(appToProfile: AppToProfile)

    @Query("SELECT * FROM app WHERE id IN (SELECT appId FROM apptoprofile WHERE profileId = :id)")
    fun getProfileAppsById(id: Long): Flow<List<App>>

    @Query("SELECT * FROM profileentity WHERE id IN (SELECT profileId FROM apptoprofile WHERE appId = :appId)")
    suspend fun getProfilesWithApp(appId: Long): List<ProfileEntity>

    @Query("SELECT COUNT(*) FROM apptoprofile WHERE appId = :appId")
    suspend fun isAppInProfiles(appId: Long): Boolean
}