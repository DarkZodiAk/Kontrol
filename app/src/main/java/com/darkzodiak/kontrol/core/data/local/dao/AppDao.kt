package com.darkzodiak.kontrol.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.darkzodiak.kontrol.core.data.local.entity.App
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Insert
    suspend fun insertApp(app: App)

    @Update
    suspend fun updateApp(app: App)

    @Transaction
    suspend fun upsertApp(app: App) {
        val id = getAppByPackageName(app.packageName)?.id

        if(id != null) updateApp(app.copy(id = id))
        else insertApp(app)
    }

    @Delete
    suspend fun deleteApp(app: App)

    @Query("DELETE FROM app WHERE packageName = :packageName")
    suspend fun deleteAppByPackageName(packageName: String)

    @Query("SELECT * FROM app")
    fun getAllApps(): Flow<List<App>>

    @Query("SELECT * FROM app WHERE id = :id")
    suspend fun getAppById(id: Long): App

    @Query("SELECT * FROM app WHERE packageName = :packageName")
    suspend fun getAppByPackageName(packageName: String): App?
}