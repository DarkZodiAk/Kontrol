package com.darkzodiak.kontrol.overlay

import android.content.Context
import android.view.LayoutInflater
import com.darkzodiak.kontrol.overlay.restrictions.PasswordOverlay
import com.darkzodiak.kontrol.overlay.restrictions.RandomTextOverlay
import com.darkzodiak.kontrol.overlay.restrictions.SimpleBlockOverlay
import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType

class OverlaysMapFactory {
    fun build(
        context: Context,
        layoutInflater: LayoutInflater,
        onClose: (Boolean) -> Unit
    ): Map<AppRestrictionType, Overlay> {
        val simpleBlockOverlay = SimpleBlockOverlay(
            context, layoutInflater, onClose
        )

        val passwordOverlay = PasswordOverlay(
            context, layoutInflater, onClose
        )

        val randomTextOverlay = RandomTextOverlay(
            context, layoutInflater, onClose
        )

        return hashMapOf(
            AppRestrictionType.SIMPLE_BLOCK to simpleBlockOverlay,
            AppRestrictionType.PASSWORD to passwordOverlay,
            AppRestrictionType.RANDOM_TEXT to randomTextOverlay
        )
    }
}