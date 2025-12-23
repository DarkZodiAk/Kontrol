package com.darkzodiak.kontrol.overlay.restrictions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.darkzodiak.kontrol.R
import com.darkzodiak.kontrol.overlay.Overlay
import com.darkzodiak.kontrol.overlay.OverlayData
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PasswordOverlay(
    context: Context,
    layoutInflater: LayoutInflater,
    onClose: (Boolean) -> Unit
): Overlay(context, layoutInflater, R.layout.password_overlay, onClose) {

    private val infoText = view.findViewById<TextView>(R.id.info_text)
    private val closeButton = view.findViewById<MaterialButton>(R.id.password_close)
    private val submitButton = view.findViewById<MaterialButton>(R.id.password_submit)
    private val passwordInput = view.findViewById<TextInputEditText>(R.id.password_edit_text)
    private val inputLayout = view.findViewById<TextInputLayout>(R.id.password_input_layout)

    override fun init(data: OverlayData) {
        if (data !is OverlayData.Password) return
        val text = "Введите пароль для получения доступа к ${data.appName}"

        infoText.text = text

        closeButton.setOnClickListener {
            passwordInput.setText("")
            close()
        }

        submitButton.setOnClickListener {
            val password = passwordInput.text.toString()
            if (password == data.password) {
                passwordInput.setText("")
                onClose(false)
            } else {
                inputLayout.isErrorEnabled = true
                inputLayout.error = "Неверный пароль"
            }
        }

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (inputLayout.isErrorEnabled) {
                    clearError(inputLayout)
                }
            }
        })

        passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitButton.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun clearError(inputLayout: TextInputLayout) {
        inputLayout.isErrorEnabled = false
        inputLayout.error = null
    }

    override fun close() {
        onClose(true)
    }
}