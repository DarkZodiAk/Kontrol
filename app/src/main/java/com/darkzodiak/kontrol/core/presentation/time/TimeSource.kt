package com.darkzodiak.kontrol.core.presentation.time

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

class TimeSource() {
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

        var timerJob: Job? = null
        // Resets time if the offset was changed
        var offsetJob = launch {
            offset.collect {
                timerJob?.cancel()
                timerJob = launchTimerJob()
            }
        }

        // Resets time if the time of system was changed
        launch {
            timeChangedCounter.collect {
                timerJob?.cancel()
                timerJob = launchTimerJob()
            }
        }

        awaitClose {
            offsetJob.cancel()
            timerJob?.cancel()
            offset.update { Duration.ZERO }
        }
    }
        .stateIn(
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = nowWithOffset()
        )

    fun setTimeOffset(duration: Duration) {
        offset.update { duration }
    }

    fun reset() {
        offset.update { Duration.ZERO }
    }

    private fun nowWithOffset() = LocalDateTime.now().plusDuration(offset.value)


    companion object {
        private const val MINUTE_MILLIS = 60000L
        private val timeChangedCounter = MutableStateFlow(0)

        fun onTimeChanged() {
            timeChangedCounter.update { timeChangedCounter.value + 1 }
        }
    }
}