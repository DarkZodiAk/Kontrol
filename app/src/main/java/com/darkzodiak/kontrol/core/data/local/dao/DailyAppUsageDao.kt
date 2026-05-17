package com.darkzodiak.kontrol.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.darkzodiak.kontrol.core.data.local.entity.DailyAppUsageEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyAppUsageDao {
    @Upsert
    suspend fun upsertUsage(appUsage: DailyAppUsageEntity)

    @Query("SELECT * FROM dailyappusageentity WHERE date = :date")
    fun getUsageForDate(date: LocalDate): Flow<List<DailyAppUsageEntity>>

    @Query("SELECT * FROM dailyappusageentity WHERE date BETWEEN :start AND :end")
    fun getUsageForDateRange(start: LocalDate, end: LocalDate): Flow<List<DailyAppUsageEntity>>
}