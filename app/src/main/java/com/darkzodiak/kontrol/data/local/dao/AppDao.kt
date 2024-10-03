package com.darkzodiak.kontrol.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.darkzodiak.kontrol.data.local.entity.App
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    suspend fun insertApp(app: App)

    @Delete
    suspend fun deleteApp(app: App)

    @Query("SELECT * FROM app")
    fun getAllApps(): Flow<List<App>>

    @Query("SELECT * FROM app WHERE id = :id")
    suspend fun getAppById(id: Long): App
}