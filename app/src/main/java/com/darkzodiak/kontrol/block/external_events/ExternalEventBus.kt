package com.darkzodiak.kontrol.block.external_events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ExternalEventBus {
    private val _bus = MutableSharedFlow<ExternalEvent>()
    val bus = _bus.asSharedFlow()

    suspend fun postEvent(event: ExternalEvent) {
        _bus.emit(event)
    }
}