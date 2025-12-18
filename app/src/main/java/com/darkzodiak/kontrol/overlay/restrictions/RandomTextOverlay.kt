package com.darkzodiak.kontrol.overlay.restrictions

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.darkzodiak.kontrol.R
import com.darkzodiak.kontrol.core.presentation.getRandomAlphaString
import com.darkzodiak.kontrol.overlay.Overlay
import com.darkzodiak.kontrol.overlay.OverlayData
import com.google.android.material.button.MaterialButton
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RandomTextOverlay(
    layoutInflater: LayoutInflater,
    @LayoutRes layoutID: Int,
    onClose: (Boolean) -> Unit
): Overlay(layoutInflater, layoutID, onClose) {

    private val infoText = view.findViewById<TextView>(R.id.info_text)
    private val randomText = view.findViewById<TextView>(R.id.random_text)
    private val closeButton = view.findViewById<MaterialButton>(R.id.random_close)
    private val inputLayout = view.findViewById<TextInputLayout>(R.id.random_input_layout)
    private val randomTextInput = view.findViewById<TextInputEditText>(R.id.random_edit_text)
    private val submitButton = view.findViewById<MaterialButton>(R.id.random_submit)

    override fun init(data: OverlayData) {
        if (data !is OverlayData.RandomText) return

        val text = "Введите текст ниже для получения доступа к ${data.appName}"
        val randomString = getRandomAlphaString(data.randomTextLength)

        infoText.text = text
        randomText.text = randomString

        closeButton.setOnClickListener {
            randomTextInput.setText("")
            close()
        }

        submitButton.setOnClickListener {
            val input = randomTextInput.text.toString()
            if (input == randomString) {
                randomTextInput.setText("")
                onClose(false)
            } else {
                inputLayout.isErrorEnabled = true
                inputLayout.error = "Текст введен неверно"
            }
        }

        randomTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (inputLayout.error != null) {
                    clearError()
                }
            }
        })
    }

    private fun clearError() {
        inputLayout.error = null
    }

    override fun close() {
        onClose(true)
    }
}