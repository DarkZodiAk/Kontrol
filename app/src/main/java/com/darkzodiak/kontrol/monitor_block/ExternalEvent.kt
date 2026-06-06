package com.darkzodiak.kontrol.monitor_block

sealed interface ExternalEvent {
    object ReturnToLauncher: ExternalEvent
    object OpenKontrol: ExternalEvent
    data class OpenApp(val packageName: String): ExternalEvent
}