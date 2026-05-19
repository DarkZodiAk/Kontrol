package com.darkzodiak.kontrol.apps.di

import com.darkzodiak.kontrol.apps.data.AppRepositoryImpl
import com.darkzodiak.kontrol.apps.domain.AppRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAppRepositoryImpl(
        appRepositoryImpl: AppRepositoryImpl
    ): AppRepository
}