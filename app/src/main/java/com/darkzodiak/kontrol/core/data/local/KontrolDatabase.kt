package com.darkzodiak.kontrol.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.darkzodiak.kontrol.core.data.local.converters.DailyAppUsageConverters
import com.darkzodiak.kontrol.core.data.local.converters.ProfileConverters
import com.darkzodiak.kontrol.core.data.local.dao.AppDao
import com.darkzodiak.kontrol.core.data.local.dao.DailyAppUsageDao
import com.darkzodiak.kontrol.core.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.core.data.local.entity.AppEntity
import com.darkzodiak.kontrol.core.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.core.data.local.entity.DailyAppUsageEntity
import com.darkzodiak.kontrol.core.data.local.entity.ProfileEntity

@Database(
    entities = [ProfileEntity::class, AppEntity::class, AppToProfile::class, DailyAppUsageEntity::class],
    version = 15
)
@TypeConverters(ProfileConverters::class, DailyAppUsageConverters::class)
abstract class KontrolDatabase: RoomDatabase() {
    abstract val profileDao: ProfileDao
    abstract val appDao: AppDao
    abstract val dailyAppUsageDao: DailyAppUsageDao

    companion object {
        const val DB_NAME = "kontrol_db"
    }
}