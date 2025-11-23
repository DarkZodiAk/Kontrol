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

class DelayDialogViewModel: ViewModel() {

    private val mainDialogTimeSource = TimeSource()
    private val selectDialogTimeSource = TimeSource()

    var state by mutableStateOf(DelayDialogState())
        private set

    init {
        mainDialogTimeSource.currentTime.onEach { time ->
            if (state.delayType == DelayType.CUSTOM && state.delayTime > time) return@onEach
            state = state.copy(delayTime = time)
        }.launchIn(viewModelScope)

        selectDialogTimeSource.currentTime.onEach { time ->
            state = state.copy(selectDelayTime = time)
        }.launchIn(viewModelScope)
    }

    fun onAction(action: DelayDialogAction) {
        when (action) {
            DelayDialogAction.Close -> {
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