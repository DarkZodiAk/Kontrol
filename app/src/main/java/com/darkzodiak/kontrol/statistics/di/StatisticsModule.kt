package com.darkzodiak.kontrol.statistics.di

import com.darkzodiak.kontrol.core.data.local.KontrolDatabase
import com.darkzodiak.kontrol.statistics.data.local.DailyAppUsageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StatisticsModule {

    @Provides
    @Singleton
    fun providesDailyAppUsageDao(db: KontrolDatabase): DailyAppUsageDao {
        return db.dailyAppUsageDao
    }
}