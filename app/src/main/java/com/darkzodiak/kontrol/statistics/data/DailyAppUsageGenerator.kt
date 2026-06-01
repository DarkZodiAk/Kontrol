package com.darkzodiak.kontrol.statistics.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import com.darkzodiak.kontrol.apps.domain.App
import com.darkzodiak.kontrol.apps.domain.AppRepository
import com.darkzodiak.kontrol.core.data.local.entity.DailyAppUsageEntity
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
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyAppUsageGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appRepository: AppRepository
) {
    private val _initialized = MutableStateFlow(false)
    val initialized = _initialized.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private val usageStatsManager = getUsageStatsManager()
    private val timeZone: ZoneId get() = ZoneId.systemDefault()
    private var apps: Map<String, App> = emptyMap()

    init {
        appRepository.getAllApps().onEach {
            apps = it.filterNot { app -> app.isDeleted }.associateBy { app -> app.packageName }
            _initialized.update { true }
        }.launchIn(scope)
    }

    fun getDailyAppUsagesForDay(date: LocalDate): List<DailyAppUsageEntity> {
        val appStats = getUsageMapForDay(date)

        return appStats.map { (app, time) ->
            DailyAppUsageEntity(
                date = date,
                appId = app.id,
                foregroundTimeMs = time
            )
        }
    }

    private fun getUsageMapForDay(date: LocalDate): Map<App, Long> {
        val dayStart = dateToLocalMidnightTimestamp(date)
        val dayEnd = dateToLocalMidnightTimestamp(date.plusDays(1))
        val nowLocal = Instant.now().atZone(timeZone).toLocalDate()

        val events = usageStatsManager.queryEvents(dayStart, dayEnd)
        val usageMap = mutableMapOf<App, Long>()
        val lastResumedMap = mutableMapOf<String, UsageEvents.Event>()

        while (events.hasNextEvent()) {
            val event = UsageEvents.Event().also { events.getNextEvent(it) }
            val packageName = event.packageName
            val app = apps[packageName] ?: continue
            val eventKey = packageName + event.className

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    lastResumedMap[eventKey] = event
                }
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    val resumeEvent = lastResumedMap[eventKey]
                    if (resumeEvent != null) {
                        val duration = event.timeStamp - resumeEvent.timeStamp
                        if (duration > 0) {
                            usageMap[app] = usageMap.getOrDefault(app, 0L) + duration
                        }
                        lastResumedMap.remove(eventKey)
                    }
                }
            }
        }

        val endTime = if (date == nowLocal) System.currentTimeMillis() else dayEnd
        lastResumedMap.entries.forEach { (_, event) ->
            val app = apps[event.packageName] ?: return@forEach
            val duration = endTime - event.timeStamp
            if (duration > 0) {
                usageMap[app] = usageMap.getOrDefault(app, 0L) + duration
            }
        }

        return usageMap
    }

    private fun dateToLocalMidnightTimestamp(date: LocalDate): Long {
        return date.atStartOfDay(timeZone).toInstant().toEpochMilli()
    }

    private fun getUsageStatsManager(): UsageStatsManager {
        val serviceString = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) "usagestats"
        else Context.USAGE_STATS_SERVICE
        return context.getSystemService(serviceString) as UsageStatsManager
    }
}