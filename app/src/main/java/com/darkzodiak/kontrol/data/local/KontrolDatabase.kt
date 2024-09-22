package com.darkzodiak.kontrol.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darkzodiak.kontrol.data.local.dao.ProfileDao

@Database(
    entities = [],
    version = 1
)
abstract class KontrolDatabase: RoomDatabase() {
    abstract val profileDao: ProfileDao

    companion object {
        const val DB_NAME = "kontrol_db"
    }
}