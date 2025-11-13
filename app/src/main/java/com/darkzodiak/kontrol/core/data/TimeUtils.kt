package com.darkzodiak.kontrol.core.data

import java.time.LocalDateTime
import java.time.ZoneId

fun millisUntil(targetDateTime: LocalDateTime): Long {
    val targetInstant = targetDateTime.atZone(ZoneId.systemDefault()).toInstant()

    val targetMillis = targetInstant.toEpochMilli()

    return targetMillis - System.currentTimeMillis()
}