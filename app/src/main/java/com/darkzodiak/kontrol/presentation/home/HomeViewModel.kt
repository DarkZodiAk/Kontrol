package com.darkzodiak.kontrol.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.domain.KontrolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: KontrolRepository,
) : ViewModel() {

    val profiles = repository.getProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun onAction(action: HomeAction) {
        when(action) {
            is HomeAction.SwitchProfileState -> {
                viewModelScope.launch {
                    repository.updateProfile(action.profile)
                }
            }
            else -> Unit
        }
    }
}