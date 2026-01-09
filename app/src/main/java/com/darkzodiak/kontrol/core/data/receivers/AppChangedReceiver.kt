package com.darkzodiak.kontrol.core.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.darkzodiak.kontrol.core.data.AppScanner
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppChangedReceiver : BroadcastReceiver() {
    @Inject lateinit var appScanner: AppScanner

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val packageName = intent.data?.schemeSpecificPart ?: return
                appScanner.onAppInstalled(packageName)
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                val packageName = intent.data?.schemeSpecificPart ?: return
                appScanner.onAppDeleted(packageName)
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                val packageName = intent.data?.schemeSpecificPart ?: return
                appScanner.onAppReplaced(packageName)
            }
        }
    }
}