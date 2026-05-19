package com.darkzodiak.kontrol.apps.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class AppChangedReceiver(private val appScanner: AppScanner) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val packageName = intent.data?.schemeSpecificPart ?: return
                appScanner.onAppInstalled(packageName)
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                val packageName = intent.data?.schemeSpecificPart ?: return
                appScanner.onAppReplaced(packageName)
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                val packageName = intent.data?.schemeSpecificPart ?: return
                appScanner.onAppDeleted(packageName)
            }
        }
    }

    companion object {
        private var registered = false

        fun register(context: Context, appScanner: AppScanner) {
            if (registered) return

            val intentFilter = IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addDataScheme("package")
            }

            val appChangedReceiver = AppChangedReceiver(appScanner)
            context.registerReceiver(appChangedReceiver, intentFilter)
        }
    }
}