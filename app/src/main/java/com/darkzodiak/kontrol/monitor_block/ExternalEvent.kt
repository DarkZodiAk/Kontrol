package com.darkzodiak.kontrol.monitor_block

sealed interface ExternalEvent {
    data class OpenApp(val packageName: String): ExternalEvent
    object OpenKontrol: ExternalEvent
    object ReturnToLauncher: ExternalEvent
}