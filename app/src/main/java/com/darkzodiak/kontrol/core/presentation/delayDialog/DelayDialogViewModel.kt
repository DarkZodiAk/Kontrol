package com.darkzodiak.kontrol.core.presentation.delayDialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.core.presentation.time.TimeSource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class DelayDialogViewModel: ViewModel() {

    private var initialized = false
    private val mainDialogTimeSource = TimeSource()
    private val selectDialogTimeSource = TimeSource()

    var state by mutableStateOf(DelayDialogState())
        private set

    init {
        mainDialogTimeSource.currentTime.onEach { time ->
            if (state.delayType == DelayType.CUSTOM && state.delayTime > time) return@onEach
            state = state.copy(delayTime = time.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES))
        }.launchIn(viewModelScope)

        selectDialogTimeSource.currentTime.onEach { time ->
            state = state.copy(selectDelayTime = time)
        }.launchIn(viewModelScope)
    }

    fun setInitialData(value: LocalDateTime?) {
        if (initialized) return
        initialized = true
        if (value == null) return
        state = state.copy(delayTime = value)
    }

    fun onAction(action: DelayDialogAction) {
        when (action) {
            DelayDialogAction.Close -> {
                initialized = false
                state = DelayDialogState()
                mainDialogTimeSource.reset()
                selectDialogTimeSource.reset()
            }
            DelayDialogAction.SaveDelayType -> {
                if (state.unsavedDelayType == state.delayType) return
                val type = state.unsavedDelayType
                state = state.copy(delayType = type)
                mainDialogTimeSource.setTimeOffset(type.delay)
            }
            DelayDialogAction.DismissDelayType -> {
                val type = state.delayType
                state = state.copy(unsavedDelayType = type)
                selectDialogTimeSource.setTimeOffset(type.delay)
            }
            is DelayDialogAction.SelectDelayType -> {
                val type = action.type
                state = state.copy(unsavedDelayType = type)
                selectDialogTimeSource.setTimeOffset(type.delay)
            }
            is DelayDialogAction.SetCustomTime -> {
                val time = if (action.time < LocalDateTime.now()) {
                    // TODO(): Show a brief warning that user can't set a pause to the past
                    LocalDateTime.now()
                } else {
                    action.time
                }
                state = state.copy(delayTime = time, delayType = DelayType.CUSTOM)
            }
        }
    }
}