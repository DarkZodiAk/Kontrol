package com.darkzodiak.kontrol.overlay

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes

abstract class Overlay (
    context: Context,
    layoutInflater: LayoutInflater,
    @LayoutRes private val layoutID: Int,
    protected val onClose: (Boolean) -> Unit
) {
    val wrapper = object : FrameLayout(context) {
        override fun dispatchKeyEvent(event: KeyEvent): Boolean {
            return if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                close()
                true
            } else {
                super.dispatchKeyEvent(event)
            }
        }
    }
    var view: View = layoutInflater.inflate(layoutID, wrapper)

    abstract fun init(data: OverlayData)

    protected abstract fun close()
}