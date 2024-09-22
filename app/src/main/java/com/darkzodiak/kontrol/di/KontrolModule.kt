package com.darkzodiak.kontrol.di

import android.content.Context
import androidx.room.Room
import com.darkzodiak.kontrol.data.local.KontrolDatabase
import com.darkzodiak.kontrol.data.local.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KontrolModule {
    @Provides
    @Singleton
    fun providesKontrolDatabase(@ApplicationContext context: Context): KontrolDatabase {
        return Room.databaseBuilder(
            context,
            KontrolDatabase::class.java,
            KontrolDatabase.DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providesProfileDao(db: KontrolDatabase): ProfileDao {
        return db.profileDao
    }
}