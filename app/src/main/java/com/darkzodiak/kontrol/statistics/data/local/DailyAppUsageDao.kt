package com.darkzodiak.kontrol.statistics.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyAppUsageDao {
    @Upsert
    suspend fun upsertUsage(appUsage: DailyAppUsageEntity)

    @Delete
    suspend fun deleteUsage(appUsage: DailyAppUsageEntity)

    @Query("SELECT * FROM dailyappusageentity WHERE date = :date")
    fun getUsageForDate(date: Long): Flow<List<DailyAppUsageEntity>>

    @Query("SELECT * FROM dailyappusageentity WHERE date BETWEEN :start AND :end")
    fun getUsageForDateRange(start: Long, end: Long): Flow<List<DailyAppUsageEntity>>
}