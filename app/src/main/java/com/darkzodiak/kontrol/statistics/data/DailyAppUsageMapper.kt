package com.darkzodiak.kontrol.statistics.data

import com.darkzodiak.kontrol.core.domain.App
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.statistics.data.local.DailyAppUsageEntity
import com.darkzodiak.kontrol.statistics.domain.DailyAppUsage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyAppUsageMapper @Inject constructor(
    private val kontrolRepository: KontrolRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private var apps: Map<Long, App> = emptyMap()

    init {
        kontrolRepository.getAllApps().onEach {
            apps = it.associateBy { app -> app.id }
        }.launchIn(scope)
    }

    fun entityToDomain(entity: DailyAppUsageEntity): DailyAppUsage {
        return DailyAppUsage(
            date = DateUtils.utcMidnightTimestampToLocalDate(entity.date),
            app = apps[entity.appId] ?: App(),
            foregroundTimeMs = entity.foregroundTimeMs,
            percentOfTotalUsage = entity.percentOfTotalUsage
        )
    }

    fun entityListToDomain(list: List<DailyAppUsageEntity>): List<DailyAppUsage> {
        return list.filter { it.appId in apps }.map(::entityToDomain)
    }
}