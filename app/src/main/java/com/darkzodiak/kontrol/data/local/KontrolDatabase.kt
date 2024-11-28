package com.darkzodiak.kontrol.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darkzodiak.kontrol.data.local.dao.AppDao
import com.darkzodiak.kontrol.data.local.dao.ProfileDao
import com.darkzodiak.kontrol.data.local.entity.AppEntity
import com.darkzodiak.kontrol.data.local.entity.AppToProfile
import com.darkzodiak.kontrol.data.local.entity.Profile

@Database(
    entities = [Profile::class, AppEntity::class, AppToProfile::class],
    version = 5
)
abstract class KontrolDatabase: RoomDatabase() {
    abstract val profileDao: ProfileDao
    abstract val appDao: AppDao

    companion object {
        const val DB_NAME = "kontrol_db"
    }
}