package com.darkzodiak.kontrol.core.presentation.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration

// TODO(): May break if user changes locale
val UIDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm")
    .withLocale(Locale.getDefault(Locale.Category.FORMAT))

val UIDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    .withLocale(Locale.getDefault(Locale.Category.FORMAT))

val UITimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    .withLocale(Locale.getDefault(Locale.Category.FORMAT))

val UIDayPlusTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM HH:mm")
    .withLocale(Locale.getDefault(Locale.Category.FORMAT))

fun LocalDateTime.plusDuration(duration: Duration): LocalDateTime = this
    .plusDays(duration.inWholeDays)
    .plusHours(duration.inWholeHours)
    .plusMinutes(duration.inWholeMinutes)

fun LocalDateTime.toFullString(): String = this.format(UIDateTimeFormatter)
fun LocalDateTime.toDateString(): String = this.format(UIDateFormatter)
fun LocalDateTime.toTimeString(): String = this.format(UITimeFormatter)
fun LocalDateTime.toDayPlusTimeString(): String = this.format(UIDayPlusTimeFormatter)