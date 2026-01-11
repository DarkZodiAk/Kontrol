package com.darkzodiak.kontrol.overlay.restrictions

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.darkzodiak.kontrol.R
import com.darkzodiak.kontrol.overlay.Overlay
import com.darkzodiak.kontrol.overlay.OverlayData

class SimpleBlockOverlay(
    context: Context,
    layoutInflater: LayoutInflater,
    onClose: (Boolean) -> Unit
): Overlay(context, layoutInflater, R.layout.simple_block_overlay, onClose) {

    override fun init(data: OverlayData) {
        if (data !is OverlayData.SimpleBlock) return

        val text = "${data.appName} заблокирован"
        view.findViewById<TextView>(R.id.restriction_text).text = text
        view.findViewById<Button>(R.id.okButton).setOnClickListener { close() }
    }

    override fun close() {
        onClose(true)
    }
}