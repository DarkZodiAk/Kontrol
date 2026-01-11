package com.darkzodiak.kontrol.overlay

import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.profile.domain.AppRestriction
import com.darkzodiak.kontrol.profile.domain.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlayDataCreator @Inject constructor(
    private val repository: KontrolRepository
) {
    suspend fun createDataFrom(packageName: String, profile: Profile): OverlayData {
        val restriction = profile.appRestriction
        val appName = repository.getAppByPackageName(packageName)?.title ?: "Неизвестное приложение"
        return when (restriction) {
            is AppRestriction.Password -> OverlayData.Password(appName, restriction.password)
            is AppRestriction.RandomText -> OverlayData.RandomText(appName, restriction.length)
            AppRestriction.SimpleBlock -> OverlayData.SimpleBlock(appName)
        }
    }
}