package com.darkzodiak.kontrol.apps.data

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.darkzodiak.kontrol.core.data.AppWideBroadcastReceiver

class AppChangedReceiver(private val appScanner: AppScanner) : AppWideBroadcastReceiver() {

    override val intentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addDataScheme("package")
    }

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
}