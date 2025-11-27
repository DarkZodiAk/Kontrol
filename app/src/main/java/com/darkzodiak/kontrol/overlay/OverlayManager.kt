package com.darkzodiak.kontrol.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.content.getSystemService
import com.darkzodiak.kontrol.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlayManager @Inject constructor(
    @ApplicationContext appContext: Context,
) {
    private val themedContext = ContextThemeWrapper(
        appContext, R.style.Theme_Kontrol
    )
    private val windowManager by lazy {
        themedContext.getSystemService<WindowManager>()!!
    }
    private val layoutInflater by lazy {
        themedContext.getSystemService<LayoutInflater>()!!
    }

    private var blockCallback: (() -> Unit)? = null
    private var blockView: View? = null

    private val overlaysMap = OverlaysMapFactory().build(
        layoutInflater = layoutInflater,
        onClose = { appShouldClose ->
            closeOverlay()
            if (appShouldClose) {
                blockCallback?.invoke()
            }
            blockCallback = null
        }
    )

    private val windowParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        },
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        PixelFormat.OPAQUE
    )

    fun openOverlay(data: OverlayData, callback: () -> Unit) {
        val overlay = overlaysMap[data.appRestrictionType] ?: return
        overlay.init(data)
        blockCallback = callback
        blockView = overlay.view

        try {
            windowManager.addView(blockView, windowParams)
        } catch (e: Exception) {
            // TODO(): Find out what exceptions we may encounter here
        }
    }

    private fun closeOverlay() {
        if (blockView == null) return
        try {
            windowManager.removeView(blockView)
        } catch (e: Exception) {
            // TODO(): Find out what exceptions we may encounter here
        }
        blockView = null
    }
}