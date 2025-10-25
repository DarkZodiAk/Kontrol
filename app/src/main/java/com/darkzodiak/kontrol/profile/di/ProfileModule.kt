package com.darkzodiak.kontrol.profile.di

import com.darkzodiak.kontrol.core.data.local.KontrolDatabase
import com.darkzodiak.kontrol.profile.data.local.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    @Singleton
    fun providesProfileDao(db: KontrolDatabase): ProfileDao {
        return db.profileDao
    }
}