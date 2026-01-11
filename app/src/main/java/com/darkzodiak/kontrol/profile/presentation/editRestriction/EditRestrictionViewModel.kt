package com.darkzodiak.kontrol.profile.presentation.editRestriction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.core.presentation.time.TimeSource
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.presentation.ProfileInterScreenBus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EditRestrictionViewModel: ViewModel() {

    private var rendered = false
    private val timeSource = TimeSource()
    private val interScreenCache = ProfileInterScreenBus.get()

    var state by mutableStateOf(EditRestrictionState())
        private set

    private val channel = Channel<EditRestrictionEvent>()
    val events = channel.receiveAsFlow()

    init {
        viewModelScope.launch {
            setRestriction(interScreenCache.editRestriction.first(), true)
        }

        timeSource.currentTime.onEach { time ->
            checkTimedRestriction(time)
        }.launchIn(viewModelScope)
    }

    fun render() {
        rendered = true
    }

    fun onAction(action: EditRestrictionAction) {
        if (rendered.not()) return
        when (action) {
            EditRestrictionAction.Dismiss -> {
                closeDialog()
                rendered = false
                sendEvent(EditRestrictionEvent.GoBack)
            }

            EditRestrictionAction.Save -> {
                closeDialog()
                rendered = false
                interScreenCache.sendEditRestriction(state.restriction)
                sendEvent(EditRestrictionEvent.GoBack)
            }

            is EditRestrictionAction.SetRestriction -> when (action.type) {
                EditRestrictionType.NO_RESTRICTION -> {
                    setRestriction(EditRestriction.NoRestriction)
                }
                EditRestrictionType.RANDOM_TEXT -> {
                    openDialog(DialogType.RANDOM_TEXT)
                }
                EditRestrictionType.PASSWORD -> {
                    openDialog(DialogType.PASSWORD)
                }
                EditRestrictionType.UNTIL_DATE -> {
                    openDialog(DialogType.UNTIL_DATE)
                }
                EditRestrictionType.UNTIL_REBOOT -> {
                    if (state.restriction is EditRestriction.UntilReboot) return
                    setRestriction(EditRestriction.UntilReboot(false))
                }
            }

            EditRestrictionAction.DismissDialog -> {
                closeDialog()
            }

            is EditRestrictionAction.SendDialogData -> {
                when (action.data) {
                    is DialogData.Password -> {
                        setRestriction(EditRestriction.Password(action.data.text))
                    }
                    is DialogData.RandomText -> {
                        setRestriction(EditRestriction.RandomText(action.data.length))
                    }
                    is DialogData.UntilDate -> {
                        val stopAfterDate = (state.restriction as? EditRestriction.UntilReboot)?.stopAfterReboot
                            ?: false

                        setRestriction(EditRestriction.UntilDate(action.data.date, stopAfterDate))
                    }
                }
                closeDialog()
            }

            is EditRestrictionAction.SwitchOption -> when (action.option) {
                OptionType.STOP_AFTER_DATE -> {
                    val restriction = state.restriction
                    if (restriction !is EditRestriction.UntilDate) return
                    setRestriction(restriction.copy(stopAfterReachingDate = restriction.stopAfterReachingDate.not()))
                }
                OptionType.STOP_AFTER_REBOOT -> {
                    val restriction = state.restriction
                    if (restriction !is EditRestriction.UntilReboot) return
                    setRestriction(restriction.copy(stopAfterReboot = restriction.stopAfterReboot.not()))
                }
            }
        }
    }

    private fun setRestriction(restriction: EditRestriction, initialSet: Boolean = false) {
        state = state.copy(restriction = restriction, unsaved = initialSet.not())
    }

    private fun openDialog(type: DialogType) {
        state = state.copy(openedDialogType = type)
    }

    private fun closeDialog() {
        state = state.copy(openedDialogType = DialogType.NONE)
    }

    private fun sendEvent(event: EditRestrictionEvent) {
        viewModelScope.launch {
            channel.send(event)
        }
    }

    private fun checkTimedRestriction(currentTime: LocalDateTime) {
        val restriction = state.restriction as? EditRestriction.UntilDate

        if (restriction != null && restriction.date <= currentTime) {
            state = state.copy(restriction = EditRestriction.NoRestriction)
            sendEvent(EditRestrictionEvent.ShowWarning(
                text = "Блокировка профиля достигла отмеченной даты и была отключена"
            ))
        }
    }
}