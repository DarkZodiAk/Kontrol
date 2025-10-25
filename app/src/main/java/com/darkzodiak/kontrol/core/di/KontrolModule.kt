package com.darkzodiak.kontrol.core.di

import android.content.Context
import androidx.room.Room
import com.darkzodiak.kontrol.core.KontrolApp
import com.darkzodiak.kontrol.core.data.local.KontrolDatabase
import com.darkzodiak.kontrol.core.data.local.dao.AppDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
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
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providesAppDao(db: KontrolDatabase): AppDao {
        return db.appDao
    }

    @Provides
    @Singleton
    fun providesApplicationScope(@ApplicationContext appContext: Context): CoroutineScope {
        return (appContext as KontrolApp).applicationScope
    }
}