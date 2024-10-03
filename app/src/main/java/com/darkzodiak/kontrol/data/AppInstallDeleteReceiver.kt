package com.darkzodiak.kontrol.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppInstallDeleteReceiver : BroadcastReceiver() {
    @Inject lateinit var appObserver: AppObserver

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                appObserver.update()
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                appObserver.update()
            }
        }
    }
}