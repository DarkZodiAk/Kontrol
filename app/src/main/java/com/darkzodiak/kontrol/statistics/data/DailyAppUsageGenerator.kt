package com.darkzodiak.kontrol.statistics.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import com.darkzodiak.kontrol.core.domain.App
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.statistics.data.local.DailyAppUsageEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class DailyAppUsageGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: KontrolRepository
) {
    private val _initialized = MutableStateFlow(false)
    val initialized = _initialized.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private val usageStatsManager = getUsageStatsManager()
    private var apps: Map<String, App> = emptyMap()

    init {
        repository.getAllApps().onEach {
            apps = it.associateBy { app -> app.packageName }
            _initialized.update { true }
        }.launchIn(scope)
    }

    fun getDailyAppUsagesForDay(date: LocalDate): List<DailyAppUsageEntity> {
        val dayStart = DateUtils.dateToUtcMidnightTimestamp(date)
        val appStats = getUsageMapForDay(date)

        val totalUsageTime = appStats.values.sum()

        return appStats.map { (app, time) ->
            val percent = if (totalUsageTime > 0) {
                ((time * 100.0) / totalUsageTime).roundToInt()
            } else 0

            DailyAppUsageEntity(
                date = dayStart,
                appId = app.id,
                foregroundTimeMs = time,
                percentOfTotalUsage = percent
            )
        }
    }

    private fun getUsageMapForDay(date: LocalDate): Map<App, Long> {
        val dayStart = DateUtils.dateToUtcMidnightTimestamp(date)
        val dayEnd = DateUtils.dateToUtcMidnightTimestamp(date.plusDays(1))
        val nowUtc = Instant.now().atOffset(ZoneOffset.UTC).toLocalDate()

        val events = usageStatsManager.queryEvents(dayStart, dayEnd)
        val usageMap = mutableMapOf<App, Long>()
        val lastResumedMap = mutableMapOf<String, Long>()

        while (events.hasNextEvent()) {
            val event = UsageEvents.Event().also { events.getNextEvent(it) }
            val app = apps[event.packageName] ?: continue

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    lastResumedMap[event.packageName] = event.timeStamp
                }
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    val resumeTime = lastResumedMap[event.packageName]
                    if (resumeTime != null) {
                        val duration = event.timeStamp - resumeTime
                        if (duration > 0) {
                            usageMap[app] = usageMap.getOrDefault(app, 0L) + duration
                        }
                        lastResumedMap.remove(event.packageName)
                    }
                }
            }
        }

        val endTime = if (date == nowUtc) System.currentTimeMillis() else dayEnd
        lastResumedMap.entries.forEach { (packageName, resumeTime) ->
            val app = apps[packageName] ?: return@forEach
            val duration = endTime - resumeTime
            if (duration > 0) {
                usageMap[app] = usageMap.getOrDefault(app, 0L) + duration
            }
        }

        return usageMap
    }

    private fun getUsageStatsManager(): UsageStatsManager {
        val serviceString = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) "usagestats"
        else Context.USAGE_STATS_SERVICE
        return context.getSystemService(serviceString) as UsageStatsManager
    }
}