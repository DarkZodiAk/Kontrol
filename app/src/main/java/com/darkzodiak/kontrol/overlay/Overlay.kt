package com.darkzodiak.kontrol.overlay

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes

abstract class Overlay (
    private val layoutInflater: LayoutInflater,
    @LayoutRes private val layoutID: Int,
    protected val onClose: (Boolean) -> Unit
) {
    var view: View = layoutInflater.inflate(layoutID, null)

    abstract fun init(data: OverlayData)

    protected abstract fun close()
}