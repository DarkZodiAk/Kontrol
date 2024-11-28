package com.darkzodiak.kontrol.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.darkzodiak.kontrol.data.local.entity.AppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    suspend fun insertApp(appEntity: AppEntity)

    @Delete
    suspend fun deleteApp(appEntity: AppEntity)

    @Query("SELECT * FROM appentity")
    fun getAllApps(): Flow<List<AppEntity>>

    @Query("SELECT * FROM appentity WHERE id = :id")
    suspend fun getAppById(id: Long): AppEntity

    @Query("SELECT * FROM appentity WHERE packageName = :packageName")
    suspend fun getAppByPackageName(packageName: String): AppEntity?
}