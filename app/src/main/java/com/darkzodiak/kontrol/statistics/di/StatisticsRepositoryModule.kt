package com.darkzodiak.kontrol.statistics.di

import com.darkzodiak.kontrol.statistics.data.StatisticsRepositoryImpl
import com.darkzodiak.kontrol.statistics.domain.StatisticsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StatisticsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindStatisticsRepositoryImpl(
        statisticsRepositoryImpl: StatisticsRepositoryImpl
    ): StatisticsRepository
}