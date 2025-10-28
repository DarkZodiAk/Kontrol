package com.darkzodiak.kontrol.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.darkzodiak.kontrol.core.data.local.dao.AppDao
import com.darkzodiak.kontrol.profile.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.profile.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.profile.data.local.entity.ProfileEntity
import com.darkzodiak.kontrol.profile.data.local.ProfileConverters
import com.darkzodiak.kontrol.scheduling.local.EventConverters
import com.darkzodiak.kontrol.scheduling.local.entity.Event

@Database(
    entities = [ProfileEntity::class, App::class, AppToProfile::class, Event::class],
    version = 7
)
@TypeConverters(ProfileConverters::class, EventConverters::class)
abstract class KontrolDatabase: RoomDatabase() {
    abstract val profileDao: ProfileDao
    abstract val appDao: AppDao

    companion object {
        const val DB_NAME = "kontrol_db"
    }
}