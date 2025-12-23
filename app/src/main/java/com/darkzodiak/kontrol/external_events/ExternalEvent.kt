package com.darkzodiak.kontrol.external_events

sealed interface ExternalEvent {
    data class OpenApp(val packageName: String): ExternalEvent
    object ReturnToLauncher: ExternalEvent
    object LockDevice: ExternalEvent
    object UnlockDevice: ExternalEvent
}