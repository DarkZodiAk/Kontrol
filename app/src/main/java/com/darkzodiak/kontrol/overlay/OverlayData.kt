package com.darkzodiak.kontrol.overlay

import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType

sealed class OverlayData(val appRestrictionType: AppRestrictionType) {
    data class SimpleBlock(val appName: String): OverlayData(AppRestrictionType.SIMPLE_BLOCK)
    // TODO(): Increase number of overlays in future
}