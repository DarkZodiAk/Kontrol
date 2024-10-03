package com.darkzodiak.kontrol.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darkzodiak.kontrol.data.local.dao.AppDao
import com.darkzodiak.kontrol.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.data.local.entity.App
import com.darkzodiak.kontrol.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.data.local.entity.Profile

@Database(
    entities = [Profile::class, App::class, AppToProfile::class],
    version = 2
)
abstract class KontrolDatabase: RoomDatabase() {
    abstract val profileDao: ProfileDao
    abstract val appDao: AppDao

    companion object {
        const val DB_NAME = "kontrol_db"
    }
}