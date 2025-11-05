package com.darkzodiak.kontrol.core.presentation.time

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration

object TimeSource {
    private const val MINUTE_MILLIS = 60000L

    private var offset = MutableStateFlow(Duration.ZERO)

    val currentTime = callbackFlow {
        fun launchTimerJob() = launch {
            send(nowWithOffset())
            while (isActive) {
                val currentMs = System.currentTimeMillis()
                val delayMs = MINUTE_MILLIS - (currentMs % MINUTE_MILLIS)
                delay(delayMs)

                send(nowWithOffset())
            }
        }

        var timerJob = launchTimerJob()
        var offsetJob = launch {
            offset.collect {
                timerJob.cancel()
                timerJob = launchTimerJob()
            }
        }

        awaitClose {
            offsetJob.cancel()
            timerJob.cancel()
            offset.update { Duration.ZERO }
        }
    }
        .stateIn(
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = LocalDateTime.now()
        )

    fun setTimeOffset(duration: Duration) {
        offset.update { duration }
    }

    private fun nowWithOffset(): LocalDateTime = LocalDateTime.now().plusDuration(offset.value)
}