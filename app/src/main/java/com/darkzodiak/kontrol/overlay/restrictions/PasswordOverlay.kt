package com.darkzodiak.kontrol.overlay.restrictions

import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.darkzodiak.kontrol.R
import com.darkzodiak.kontrol.overlay.Overlay
import com.darkzodiak.kontrol.overlay.OverlayData

class PasswordOverlay(
    layoutInflater: LayoutInflater,
    @LayoutRes layoutID: Int,
    onClose: (Boolean) -> Unit
): Overlay(layoutInflater, layoutID, onClose) {

    override fun init(data: OverlayData) {
        if (data !is OverlayData.Password) return

        val text = "Введите пароль для получения доступа к ${data.appName}"
        view.findViewById<TextView>(R.id.password_text).text = text
        view.findViewById<Button>(R.id.password_close).setOnClickListener { close() }
        view.findViewById<Button>(R.id.password_submit).setOnClickListener {  }
//        view.findViewById<TextInputLayout>(R.id.password_input_layout)
    }



    override fun close() {
        onClose(true)
    }
}