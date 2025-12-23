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
import com.darkzodiak.kontrol.external_events.ExternalEvent
import com.darkzodiak.kontrol.external_events.ExternalEventBus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val scope = CoroutineScope(Dispatchers.IO)

    private var blockCallback: (() -> Unit)? = null
    private var proceedCallback: (() -> Unit)? = null
    private var blockView: View? = null

    private val overlaysMap = OverlaysMapFactory().build(
        context = themedContext,
        layoutInflater = layoutInflater,
        onClose = { appShouldClose ->
            if (appShouldClose) {
                blockCallback?.invoke()
            } else {
                proceedCallback?.invoke()
            }
            closeOverlay()
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
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        PixelFormat.TRANSLUCENT
    )

    init {
        ExternalEventBus.bus
            .filterIsInstance(ExternalEvent.ReturnToLauncher::class)
            .onEach { closeOverlay() }
            .launchIn(scope)
    }

    fun openOverlay(
        data: OverlayData,
        onBlock: () -> Unit = { },
        onProceed: () -> Unit = { }
    ) {
        val overlay = overlaysMap[data.appRestrictionType] ?: return
        overlay.init(data)
        blockCallback = onBlock
        proceedCallback = onProceed
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
        blockCallback = null
        proceedCallback = null
    }
}