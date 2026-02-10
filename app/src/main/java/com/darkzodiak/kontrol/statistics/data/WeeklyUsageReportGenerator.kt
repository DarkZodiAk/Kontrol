package com.darkzodiak.kontrol.statistics.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.permission.hasUsageStatsPermission
import com.darkzodiak.kontrol.statistics.domain.DailyAppUsage
import com.darkzodiak.kontrol.statistics.domain.DailyUsageReport
import com.darkzodiak.kontrol.statistics.presentation.Week
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeeklyUsageReportGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: KontrolRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val usageStatsManager = getUsageStatsManager()
    private var apps: Map<String, App> = emptyMap()

    init {
        repository.getAllApps().onEach {
            apps = it.associateBy { app -> app.packageName }
        }.launchIn(scope)
    }

    fun generateReport(week: Week): List<DailyUsageReport>? {
        if (context.hasUsageStatsPermission().not()) {
            return null
        }

        val dailyReports = mutableListOf<DailyUsageReport>()
        var currentDate = week.firstDate

        repeat(7) {
            dailyReports.add(generateDailyUsageReport(currentDate))
            currentDate = currentDate.plusDays(1)
        }

        return dailyReports
    }

    private fun generateDailyUsageReport(date: LocalDate): DailyUsageReport {
        val appStats = getUsageMapForDay(date)

        val totalUsageTime = appStats.values.sum()

        val appUsages = appStats.mapNotNull { (app, time) ->
            val percent = if (totalUsageTime > 0) {
                time * 100 / totalUsageTime
            } else 0

            DailyAppUsage(
                app = app,
                foregroundTimeMs = time,
                percentOfTotalUsage = percent
            )
        }.sortedByDescending { it.foregroundTimeMs }

        return DailyUsageReport(
            date = date,
            totalUsageTimeMs = totalUsageTime,
            appUsages = appUsages
        )
    }

    private fun getUsageMapForDay(date: LocalDate): Map<App, Long> {
        val dayStart = date.toStartTimestamp()
        val dayEnd = date.plusDays(1).toStartTimestamp()
        val events = usageStatsManager.queryEvents(dayStart, dayEnd)

        val usageMap = mutableMapOf<App, Long>()
        val lastResumedEvents = mutableMapOf<String, UsageEvents.Event>()

        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)

            if (event.packageName !in apps) continue
            val app = apps[event.packageName] ?: continue

            val currentTimeStamp = event.timeStamp
            val eventKey = app.packageName + event.className

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> lastResumedEvents[eventKey] = event
                UsageEvents.Event.ACTIVITY_PAUSED, UsageEvents.Event.ACTIVITY_STOPPED -> {
                    lastResumedEvents.remove(eventKey)?.let { lastResumedEvent ->
                        if (currentTimeStamp > dayStart) {
                            val resumeTimeStamp = maxOf(lastResumedEvent.timeStamp, dayStart)
                            usageMap[app] = usageMap.getOrDefault(
                                app,
                                0L
                            ) + (currentTimeStamp - resumeTimeStamp)
                        }
                    }
                }
                else -> {}
            }
        }

        lastResumedEvents.values
            .maxByOrNull { it.timeStamp }
            ?.let { event ->
                val app = apps[event.packageName] ?: return@let
                val usageTime = usageMap.getOrDefault(app, 0L)
                usageMap[app] = usageTime + (dayEnd - event.timeStamp)
            }

        return usageMap
    }

    private fun LocalDate.toStartTimestamp() =
        this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private fun getUsageStatsManager(): UsageStatsManager {
        val serviceString = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) "usagestats"
                            else Context.USAGE_STATS_SERVICE
        return context.getSystemService(serviceString) as UsageStatsManager
    }
}