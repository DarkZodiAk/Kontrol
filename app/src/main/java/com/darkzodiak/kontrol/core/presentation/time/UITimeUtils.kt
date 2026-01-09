package com.darkzodiak.kontrol.core.presentation.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration

object UITimeUtils {
    private var currentLocale: Locale? = null
    private val formatters = mutableMapOf<UITimeFormat, DateTimeFormatter>()

    fun formatDateTime(dateTime: LocalDateTime) = format(dateTime, UITimeFormat.DATE_TIME)
    fun formatDate(dateTime: LocalDateTime) = format(dateTime, UITimeFormat.DATE)
    fun formatTime(dateTime: LocalDateTime) = format(dateTime, UITimeFormat.TIME)
    fun formatDayAndTime(dateTime: LocalDateTime) = format(dateTime, UITimeFormat.DAY_PLUS_TIME)

    fun addDuration(dateTime: LocalDateTime, duration: Duration): LocalDateTime {
        return dateTime.plusMinutes(duration.inWholeMinutes)
    }

    private fun format(
        dateTime: LocalDateTime,
        format: UITimeFormat
    ): String {
        checkLocale()
        return dateTime.format(formatters[format])
    }

    private fun checkLocale() {
        val locale = Locale.getDefault()
        if (currentLocale == locale) return
        currentLocale = locale
        UITimeFormat.entries.onEach { format ->
            formatters[format] = DateTimeFormatter.ofPattern(format.pattern)
                .withLocale(currentLocale)
        }
    }
}
