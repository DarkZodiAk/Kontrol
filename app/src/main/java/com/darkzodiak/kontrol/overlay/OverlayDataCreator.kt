package com.darkzodiak.kontrol.overlay

import com.darkzodiak.kontrol.profile.domain.AppRestriction
import com.darkzodiak.kontrol.profile.domain.Profile

class OverlayDataCreator {
    fun createDataFrom(appName: String, profile: Profile): OverlayData {
        val restriction = profile.appRestriction
        return when (restriction) {
            is AppRestriction.Password -> OverlayData.Password(appName, restriction.password)
            is AppRestriction.RandomText -> OverlayData.RandomText(appName, restriction.length)
            AppRestriction.SimpleBlock -> OverlayData.SimpleBlock(appName)
        }
    }
}