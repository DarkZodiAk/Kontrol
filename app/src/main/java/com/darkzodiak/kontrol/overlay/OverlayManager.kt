package com.darkzodiak.kontrol.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
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
        closeOverlayImmediately()

        val overlay = overlaysMap[data.appRestrictionType] ?: return
        overlay.init(data)
        blockCallback = onBlock
        proceedCallback = onProceed
        blockView = overlay.view

        blockView?.let { view ->
            try {
                view.alpha = 0f
                windowManager.addView(view, windowParams)
                view.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            } catch (e: SecurityException) {
                Log.d("Kontrol Log", "Attempted to open overlay without overlay permission")
            } catch (e: Exception) {
                Log.d("Kontrol Log", "${e.cause} happened during overlay add")
            }
        }
    }

    private fun closeOverlay() {
        blockView?.let { view ->
            try {
                view.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setInterpolator(AccelerateInterpolator())
                    .withEndAction {
                        windowManager.removeViewImmediate(view)
                        resetOverlayState()
                    }
            } catch (e: Exception) {
                Log.d("Kontrol Log", "${e.cause} occurred during overlay exit")
            }
        } ?: resetOverlayState()
    }

    private fun closeOverlayImmediately() {
        blockView?.let { view ->
            try {
                windowManager.removeViewImmediate(view)
            } catch (e: IllegalArgumentException) {
                Log.d("Kontrol Log", "Overlay already removed: ${e.message}")
            } catch (e: Exception) {
                Log.d("Kontrol Log", "Immediate overlay removal failed: ${e.cause}")
            }
        }
        resetOverlayState()
    }

    private fun resetOverlayState() {
        blockView = null
        blockCallback = null
        proceedCallback = null
    }
}