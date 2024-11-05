package com.darkzodiak.kontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.data.AppObserver
import com.darkzodiak.kontrol.domain.eventBus.PermissionEvent
import com.darkzodiak.kontrol.domain.eventBus.PermissionEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appObserver: AppObserver
): ViewModel() {
    private val channel = Channel<MainEvent>()
    val serviceEvent = channel.receiveAsFlow()

    init {
        appObserver.update()
        PermissionEventBus.permissionBus.onEach { event ->
            when(event) {
                PermissionEvent.GrantedAllPermissions -> {
                    channel.send(MainEvent.StartKontrolService)
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }
}