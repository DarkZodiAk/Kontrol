package com.darkzodiak.kontrol.overlay

import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.darkzodiak.kontrol.R

class SimpleBlockOverlay(
    layoutInflater: LayoutInflater,
    @LayoutRes layoutID: Int,
    onClose: (Boolean) -> Unit
): Overlay(layoutInflater, layoutID, onClose) {

    override fun init(data: OverlayData) {
        if (data !is OverlayData.SimpleBlock) return

        val text = "${data.appName} заблокирован"
        view.findViewById<TextView>(R.id.appName).text = text
        view.findViewById<Button>(R.id.okButton).setOnClickListener { close() }
    }

    override fun close() {
        onClose(false)
    }
}