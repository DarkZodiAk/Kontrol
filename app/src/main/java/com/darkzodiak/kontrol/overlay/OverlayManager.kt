package com.darkzodiak.kontrol.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
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
    private val mainHandler = Handler(Looper.getMainLooper())

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

    fun openOverlay(
        data: OverlayData,
        onBlock: () -> Unit = { },
        onProceed: () -> Unit = { }
    ) {
        closeOverlayImmediately()

        val overlay = overlaysMap[data.appRestrictionType] ?: return
        overlay.init(data)
        val view = overlay.view

        runOnMainThread {
            try {
                view.animate().cancel()
                view.alpha = 0f
                windowManager.addView(view, windowParams)

                blockCallback = onBlock
                proceedCallback = onProceed
                blockView = overlay.view

                view.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            } catch (e: SecurityException) {
                Log.e("Kontrol Log", "Attempted to open overlay without overlay permission")
            } catch (e: Exception) {
                Log.e("Kontrol Log", "Overlay add failed", e)
                closeOverlayImmediately()
            }
        }
    }

    fun closeOverlay() = runOnMainThread {
        blockView?.let { view ->
            try {
                view.animate().cancel()
                view.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setInterpolator(AccelerateInterpolator())
                    .withEndAction {
                        safeRemoveView(view)
                        resetOverlayState()
                    }.start()
            } catch (e: Exception) {
                Log.e("Kontrol Log", "Overlay close failed", e)
                closeOverlayImmediately()
            }
        } ?: resetOverlayState()
    }

    fun closeOverlayImmediately() = runOnMainThread {
        blockView?.let { view ->
            view.animate().cancel()
            safeRemoveView(view)
        }
        resetOverlayState()
    }

    private fun safeRemoveView(view: View) = runOnMainThread {
        try {
            windowManager.removeViewImmediate(view)
        } catch (e: IllegalArgumentException) {
            Log.e("Kontrol Log", "Overlay already removed", e)
        } catch (e: Exception) {
            Log.e("Kontrol Log", "Overlay close failed", e)
        }
    }

    private fun runOnMainThread(block: () -> Unit) {
        mainHandler.post(block)
    }

    private fun resetOverlayState() {
        blockView = null
        blockCallback = null
        proceedCallback = null
    }
}