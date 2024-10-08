package com.darkzodiak.kontrol

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.darkzodiak.kontrol.data.AppObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appObserver: AppObserver
): ViewModel() {
    var hasPermissions by mutableStateOf(true)
        private set

    private val channel = Channel<Boolean>()
    val uiEvent = channel.receiveAsFlow()

    fun updatePermissionState() {

    }
}