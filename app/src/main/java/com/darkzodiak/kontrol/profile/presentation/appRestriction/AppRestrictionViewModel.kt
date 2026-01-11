package com.darkzodiak.kontrol.profile.presentation.appRestriction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType
import com.darkzodiak.kontrol.profile.domain.AppRestriction
import com.darkzodiak.kontrol.profile.presentation.ProfileInterScreenBus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AppRestrictionViewModel: ViewModel() {

    private var rendered = false
    private val interScreenCache = ProfileInterScreenBus.get()
    
    var state by mutableStateOf(AppRestrictionState())
        private set

    private val channel = Channel<AppRestrictionEvent>()
    val events = channel.receiveAsFlow()

    init {
        viewModelScope.launch {
            setRestriction(interScreenCache.appRestriction.first(), true)
        }
    }
    
    fun render() {
        rendered = true
    }
    
    fun onAction(action: AppRestrictionAction) {
        if (rendered.not()) return
        when (action) {
            AppRestrictionAction.Dismiss -> {
                closeDialog()
                rendered = false
                sendEvent(AppRestrictionEvent.GoBack)
            }

            AppRestrictionAction.Save -> {
                closeDialog()
                rendered = false
                interScreenCache.sendAppRestriction(state.restriction)
                sendEvent(AppRestrictionEvent.GoBack)
            }

            is AppRestrictionAction.SetRestriction -> when (action.type) {
                AppRestrictionType.SIMPLE_BLOCK -> {
                    setRestriction(AppRestriction.SimpleBlock)
                }
                AppRestrictionType.RANDOM_TEXT -> {
                    openDialog(DialogType.RANDOM_TEXT)
                }
                AppRestrictionType.PASSWORD -> {
                    openDialog(DialogType.PASSWORD)
                }
            }

            AppRestrictionAction.DismissDialog -> {
                closeDialog()
            }

            is AppRestrictionAction.SendDialogData -> {
                when (action.data) {
                    is DialogData.Password -> {
                        setRestriction(AppRestriction.Password(action.data.text))
                    }
                    is DialogData.RandomText -> {
                        setRestriction(AppRestriction.RandomText(action.data.length))
                    }
                }
                closeDialog()
            }
        }
    }

    private fun setRestriction(restriction: AppRestriction, initialSet: Boolean = false) {
        state = state.copy(restriction = restriction, unsaved = initialSet.not())
    }

    private fun openDialog(type: DialogType) {
        state = state.copy(openedDialogType = type)
    }

    private fun closeDialog() {
        state = state.copy(openedDialogType = DialogType.NONE)
    }

    private fun sendEvent(event: AppRestrictionEvent) {
        viewModelScope.launch {
            channel.send(event)
        }
    }
}