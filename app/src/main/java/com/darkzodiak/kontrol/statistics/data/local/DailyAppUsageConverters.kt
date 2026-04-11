package com.darkzodiak.kontrol.statistics.data.local

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyAppUsageConverters {
    @TypeConverter
    fun toUntilDateTime(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, dateFormatter) }
    }

    @TypeConverter
    fun fromUntilDateTime(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    companion object {
        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }
}