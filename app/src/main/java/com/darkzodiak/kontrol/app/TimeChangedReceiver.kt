package com.darkzodiak.kontrol.app

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.darkzodiak.kontrol.core.data.AppWideBroadcastReceiver
import com.darkzodiak.kontrol.core.domain.TimeSource
import com.darkzodiak.kontrol.profile.domain.ProfileRepository
import com.darkzodiak.kontrol.statistics.domain.StatisticsRepository

class TimeChangedReceiver(
    private val profileRepository: ProfileRepository,
    private val statisticsRepository: StatisticsRepository
): AppWideBroadcastReceiver() {

    override val intentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_TIME_CHANGED)
        addAction(Intent.ACTION_TIMEZONE_CHANGED)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intentFilter.hasAction(intent.action)) {
            TimeSource.onTimeChanged()
            profileRepository.actualizeAllProfiles()
            statisticsRepository.updateAllStatistics()
        }
    }
}