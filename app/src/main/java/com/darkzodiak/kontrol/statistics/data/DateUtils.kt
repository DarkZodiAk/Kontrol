package com.darkzodiak.kontrol.statistics.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

object DateUtils {
    fun utcMidnightTimestampToLocalDate(timestamp: Long): LocalDate {
        return Instant.ofEpochMilli(timestamp)
            .atOffset(ZoneOffset.UTC)
            .toLocalDate()
    }

    fun dateToUtcMidnightTimestamp(date: LocalDate): Long {
        return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}