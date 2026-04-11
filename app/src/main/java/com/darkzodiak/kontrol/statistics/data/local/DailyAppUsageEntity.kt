package com.darkzodiak.kontrol.statistics.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.darkzodiak.kontrol.core.data.local.entity.AppEntity
import java.time.LocalDate

@Entity(
    primaryKeys = ["date", "appId"],
    foreignKeys = [ForeignKey(
        entity = AppEntity::class,
        parentColumns = ["id"],
        childColumns = ["appId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["date"])]
)
data class DailyAppUsageEntity(
    val date: LocalDate,
    val appId: Long,
    val foregroundTimeMs: Long = 0L,
    val percentOfTotalUsage: Int = 0
)