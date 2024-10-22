package com.darkzodiak.kontrol.di

import com.darkzodiak.kontrol.data.KontrolRepositoryImpl
import com.darkzodiak.kontrol.domain.KontrolRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class KontrolRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindKontrolRepositoryImpl(
        kontrolRepositoryImpl: KontrolRepositoryImpl
    ): KontrolRepository
}