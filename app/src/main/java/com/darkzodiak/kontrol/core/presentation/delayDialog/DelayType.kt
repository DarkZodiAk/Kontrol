package com.darkzodiak.kontrol.core.presentation.delayDialog


import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

enum class DelayType(val text: String, val delay: Duration) {
    CUSTOM("Выбранное время", Duration.ZERO),
    MINUTES_5("5 минут", 5.minutes),
    MINUTES_10("10 минут", 10.minutes),
    MINUTES_15("15 минут", 15.minutes),
    MINUTES_20("20 минут", 20.minutes),
    MINUTES_25("25 минут", 25.minutes),
    MINUTES_30("30 минут", 30.minutes),
    MINUTES_40("40 минут", 40.minutes),
    MINUTES_45("45 минут", 45.minutes),
    MINUTES_50("50 минут", 50.minutes),
    HOURS_1("Час", 1.hours),
    MINUTES_90("1.5 часа", 90.minutes),
    HOURS_2("2 часа", 2.hours),
    HOURS_3("3 часа", 3.hours),
    HOURS_4("4 часа", 4.hours),
    HOURS_5("5 часов", 5.hours),
    HOURS_6("6 часов", 6.hours),
    HOURS_7("7 часов", 7.hours),
    HOURS_8("8 часов", 8.hours),
    HOURS_12("12 часов", 12.hours),
    HOURS_18("18 часов", 18.hours),
    DAYS_1("День", 1.days),
    DAYS_2("2 дня", 2.days),
    DAYS_3("3 дня", 3.days),
    DAYS_4("4 дня", 4.days),
    DAYS_5("5 дней", 5.days),
    DAYS_6("6 дней", 6.days),
    WEEKS_1("Неделя", 7.days),
    WEEKS_2("2 недели", 14.days),
}