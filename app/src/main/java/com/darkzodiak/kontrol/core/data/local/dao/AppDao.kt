package com.darkzodiak.kontrol.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.darkzodiak.kontrol.core.data.local.entity.AppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Insert
    suspend fun insertApp(appEntity: AppEntity)

    @Update
    suspend fun updateApp(appEntity: AppEntity)

    @Transaction
    suspend fun upsertApp(appEntity: AppEntity) {
        val id = getAppByPackageName(appEntity.packageName)?.id

        if(id != null) updateApp(appEntity.copy(id = id))
        else insertApp(appEntity)
    }

    @Delete
    suspend fun deleteApp(appEntity: AppEntity)

    @Query("DELETE FROM appentity WHERE id = :id")
    suspend fun deleteAppById(id: Long)

    @Query("SELECT * FROM appentity")
    fun getAllApps(): Flow<List<AppEntity>>

    @Query("SELECT * FROM appentity WHERE id = :id")
    suspend fun getAppById(id: Long): AppEntity?

    @Query("SELECT * FROM appentity WHERE packageName = :packageName")
    suspend fun getAppByPackageName(packageName: String): AppEntity?
}