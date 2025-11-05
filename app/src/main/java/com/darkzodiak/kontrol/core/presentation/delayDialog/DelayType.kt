package com.darkzodiak.kontrol.core.presentation.delayDialog


import kotlin.time.Duration
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
    MINUTES_50("50 минут", 50.minutes),
    HOURS_1("Час", 1.hours),
    MINUTES_90("Полтора часа", 90.minutes)
}