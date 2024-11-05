package com.darkzodiak.kontrol.domain.eventBus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PermissionEventBus {
    private val _bus = MutableSharedFlow<PermissionEvent>(extraBufferCapacity = 1)
    val permissionBus = _bus.asSharedFlow()

    suspend fun sendEvent(event: PermissionEvent) {
        _bus.emit(event)
    }
}