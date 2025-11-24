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

@Database(
    entities = [ProfileEntity::class, App::class, AppToProfile::class],
    version = 10
)
@TypeConverters(ProfileConverters::class)
abstract class KontrolDatabase: RoomDatabase() {
    abstract val profileDao: ProfileDao
    abstract val appDao: AppDao

    companion object {
        const val DB_NAME = "kontrol_db"
    }
}