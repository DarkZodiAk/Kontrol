package com.darkzodiak.kontrol.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.darkzodiak.kontrol.data.AppFetcher
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppInstallDeleteReceiver : BroadcastReceiver() {
    @Inject lateinit var appFetcher: AppFetcher

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                appFetcher.update()
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                appFetcher.update()
            }
        }
    }
}