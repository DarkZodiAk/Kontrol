package com.darkzodiak.kontrol

sealed interface MainEvent {
    object StartKontrolService: MainEvent
    object StopKotrolService: MainEvent
}