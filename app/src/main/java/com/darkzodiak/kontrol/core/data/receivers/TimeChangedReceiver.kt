package com.darkzodiak.kontrol.core.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.darkzodiak.kontrol.profile.data.ProfileActualizer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimeChangedReceiver: BroadcastReceiver() {

    @Inject
    lateinit var actualizer: ProfileActualizer

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action in actions) {
            actualizer.actualizeAll()
        }
    }

    companion object {
        private val actions = setOf(
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED
        )
    }
}