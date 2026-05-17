package com.darkzodiak.kontrol.profile.di

import com.darkzodiak.kontrol.profile.data.ProfileRepositoryImpl
import com.darkzodiak.kontrol.profile.domain.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindProfileRepositoryImpl(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
}