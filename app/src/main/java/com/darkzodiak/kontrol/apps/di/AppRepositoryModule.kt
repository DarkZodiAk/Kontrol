package com.darkzodiak.kontrol.apps.di

import com.darkzodiak.kontrol.apps.data.AppRepositoryImpl
import com.darkzodiak.kontrol.apps.domain.AppRepository
import dagger.Binds
import javax.inject.Singleton

abstract class AppRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAppRepositoryImpl(
        appRepositoryImpl: AppRepositoryImpl
    ): AppRepository
}