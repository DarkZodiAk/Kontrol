package com.darkzodiak.kontrol.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.getSystemService
import com.darkzodiak.kontrol.R

class OverlayManager(
    private val context: Context
) {
    private val windowManager by lazy {
        context.getSystemService<WindowManager>()!!
    }
    private val layoutInflater by lazy {
        context.getSystemService<LayoutInflater>()!!
    }
    private val rootView = layoutInflater.inflate(R.layout.overlay, null)

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


    private fun initWindow() {
        rootView.findViewById<View>(R.id.button2).setOnClickListener { close() }
    }

    init {
        initWindow()
    }

    fun open(text: String) {
        try {
            rootView.findViewById<TextView>(R.id.textView7).text = text
            windowManager.addView(rootView, windowParams)
        } catch (e: Exception) {
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }
    private fun close() {
        try {
            windowManager.removeView(rootView)
        } catch (e: Exception) {
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }
}