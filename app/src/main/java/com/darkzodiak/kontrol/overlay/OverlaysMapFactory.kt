package com.darkzodiak.kontrol.overlay

import android.view.LayoutInflater
import com.darkzodiak.kontrol.R
import com.darkzodiak.kontrol.overlay.restrictions.PasswordOverlay
import com.darkzodiak.kontrol.overlay.restrictions.RandomTextOverlay
import com.darkzodiak.kontrol.overlay.restrictions.SimpleBlockOverlay
import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType

class OverlaysMapFactory {
    fun build(
        layoutInflater: LayoutInflater,
        onClose: (Boolean) -> Unit
    ): Map<AppRestrictionType, Overlay> {
        val simpleBlockOverlay = SimpleBlockOverlay(
            layoutInflater, simpleBlockOverlayLayout, onClose
        )

        val passwordOverlay = PasswordOverlay(
            layoutInflater, passwordOverlayLayout, onClose
        )

        val randomTextOverlay = RandomTextOverlay(
            layoutInflater, randomTextOverlayLayout, onClose
        )

        return hashMapOf(
            AppRestrictionType.SIMPLE_BLOCK to simpleBlockOverlay,
            AppRestrictionType.PASSWORD to passwordOverlay,
            AppRestrictionType.RANDOM_TEXT to randomTextOverlay
        )
    }

    companion object {
        private val simpleBlockOverlayLayout = R.layout.simple_block_overlay
        private val passwordOverlayLayout = R.layout.password_overlay
        private val randomTextOverlayLayout = R.layout.random_text_overlay
    }
}