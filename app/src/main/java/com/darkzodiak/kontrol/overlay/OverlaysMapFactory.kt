package com.darkzodiak.kontrol.overlay

import android.view.LayoutInflater
import com.darkzodiak.kontrol.R
import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType

class OverlaysMapFactory {
    fun build(
        layoutInflater: LayoutInflater,
        onClose: (Boolean) -> Unit
    ): Map<AppRestrictionType, Overlay> {
        val simpleBlockOverlay = SimpleBlockOverlay(
            layoutInflater, simpleBlockOverlayLayout, onClose
        )

        return hashMapOf(
            AppRestrictionType.SIMPLE_BLOCK to simpleBlockOverlay
        )
    }

    companion object {
        private val simpleBlockOverlayLayout = R.layout.simple_block_overlay
    }
}