package com.darkzodiak.kontrol.core.data.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class DynamicReceiverRegistrator(private val context: Context) {
    fun register() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        val appChangedReceiver = AppChangedReceiver()
        context.registerReceiver(appChangedReceiver, intentFilter)
    }
}