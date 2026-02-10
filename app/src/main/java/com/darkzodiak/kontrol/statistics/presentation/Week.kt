package com.darkzodiak.kontrol.statistics.presentation

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

data class Week(
    val firstDate: LocalDate,
    val lastDate: LocalDate
) {
    val firstDayOfWeek: DayOfWeek = firstDate.dayOfWeek

    fun nextWeek(): Week {
        return Week(
            firstDate = firstDate.plusWeeks(1),
            lastDate = lastDate.plusWeeks(1)
        )
    }

    fun prevWeek(): Week {
        return Week(
            firstDate = firstDate.minusWeeks(1),
            lastDate = lastDate.minusWeeks(1)
        )
    }

    fun formatRange(): String {
        val currentYear = LocalDate.now().year
        val locale = Locale.getDefault()

        return when {
            firstDate.year != lastDate.year -> {
                buildString {
                    append(formatDatePart(firstDate, TextStyle.SHORT, locale))
                    append(" - ")
                    append(formatDatePart(lastDate, TextStyle.SHORT, locale))
                }
            }
            firstDate.month != lastDate.month -> {
                val base = "${firstDate.dayOfMonth} ${getMonthName(firstDate, TextStyle.SHORT, locale)} - " +
                        "${lastDate.dayOfMonth} ${getMonthName(lastDate, TextStyle.SHORT, locale)}"
                if (firstDate.year != currentYear) "$base ${firstDate.year}" else base
            }
            else -> {
                val monthName = getMonthName(firstDate, TextStyle.FULL, locale)
                val base = "${firstDate.dayOfMonth} - ${lastDate.dayOfMonth} $monthName"
                if (firstDate.year != currentYear) "$base ${firstDate.year}" else base
            }
        }
    }

    private fun formatDatePart(date: LocalDate, textStyle: TextStyle, locale: Locale): String {
        return "${date.dayOfMonth} ${getMonthName(date, textStyle, locale)} ${date.year}"
    }

    private fun getMonthName(date: LocalDate, textStyle: TextStyle, locale: Locale): String {
        return date.month.getDisplayName(textStyle, locale).capitalize(locale)
    }

    companion object {
        fun currentWeek(locale: Locale = Locale.getDefault()): Week {
            val date = LocalDate.now()
            val weekFields = WeekFields.of(locale)
            val firstDay = date.with(weekFields.dayOfWeek(), 1)
            val lastDay = firstDay.plusDays(6)
            return Week(firstDay, lastDay)
        }
    }
}