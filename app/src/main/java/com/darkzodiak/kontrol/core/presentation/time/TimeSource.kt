@file:OptIn(ExperimentalCoroutinesApi::class)

package com.darkzodiak.kontrol.core.presentation.time

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import java.time.LocalDateTime
import kotlin.time.Duration

class TimeSource(private val scope: CoroutineScope) {
    private val offset = MutableStateFlow(Duration.ZERO)

    val currentTime: StateFlow<LocalDateTime> = merge(
        offset.map { }, timeChangedSignal
    ).flatMapLatest {
        flow {
            while (currentCoroutineContext().isActive) {
                emit(nowWithOffset())
                val next = MINUTE_MILLIS - System.currentTimeMillis() % MINUTE_MILLIS
                delay(next)
            }
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = nowWithOffset()
    )

    fun setTimeOffset(duration: Duration) = offset.update { duration }
    fun reset() = offset.update { Duration.ZERO }

    private fun nowWithOffset(): LocalDateTime =
        UITimeUtils.addDuration(LocalDateTime.now(), offset.value)

    companion object {
        private const val MINUTE_MILLIS = 60_000L
        private val timeChangedSignal = MutableSharedFlow<Unit>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        fun onTimeChanged() = timeChangedSignal.tryEmit(Unit)
    }
}