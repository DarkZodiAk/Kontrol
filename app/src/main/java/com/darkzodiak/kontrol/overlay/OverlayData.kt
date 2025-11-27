package com.darkzodiak.kontrol.overlay

import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType

sealed class OverlayData(val appRestrictionType: AppRestrictionType) {
    data class SimpleBlock(val appName: String): OverlayData(AppRestrictionType.SIMPLE_BLOCK)
    data class Password(
        val appName: String,
        val password: String
    ): OverlayData(AppRestrictionType.PASSWORD)
    data class RandomText(
        val appName: String,
        val randomTextLength: Int
    ): OverlayData(AppRestrictionType.RANDOM_TEXT)
}