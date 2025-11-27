package com.darkzodiak.kontrol.overlay.restrictions

import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.darkzodiak.kontrol.R
import com.darkzodiak.kontrol.overlay.Overlay
import com.darkzodiak.kontrol.overlay.OverlayData

class SimpleBlockOverlay(
    layoutInflater: LayoutInflater,
    @LayoutRes layoutID: Int,
    onClose: (Boolean) -> Unit
): Overlay(layoutInflater, layoutID, onClose) {

    override fun init(data: OverlayData) {
        if (data !is OverlayData.SimpleBlock) return

        val text = "${data.appName} заблокирован"
        view.findViewById<TextView>(R.id.simpleBlockText).text = text
        view.findViewById<Button>(R.id.okButton).setOnClickListener { close() }
    }

    override fun close() {
        onClose(false)
    }
}