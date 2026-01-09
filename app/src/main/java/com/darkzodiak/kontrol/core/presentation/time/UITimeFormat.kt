package com.darkzodiak.kontrol.core.presentation.time

enum class UITimeFormat(val pattern: String) {
    DATE_TIME("d MMMM yyyy HH:mm"),
    DATE("d MMMM yyyy"),
    TIME("HH:mm"),
    DAY_PLUS_TIME("d MMMM HH:mm")
}