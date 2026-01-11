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
    suspend fun createDataFrom(
        packageName: String,
        profile: Profile,
        isProfileOverlapped: Boolean = false
    ): OverlayData {
        val restriction = profile.appRestriction
        val appName = repository.getAppByPackageName(packageName)?.title ?: "Неизвестное приложение"
        val profileName = if (isProfileOverlapped) profile.name else null
        return when (restriction) {
            is AppRestriction.Password -> OverlayData.Password(
                appName = appName,
                password = restriction.password,
                profileName = profileName
            )
            is AppRestriction.RandomText -> OverlayData.RandomText(
                appName = appName,
                randomTextLength = restriction.length,
                profileName = profileName
            )
            AppRestriction.SimpleBlock -> OverlayData.SimpleBlock(appName)
        }
    }
}