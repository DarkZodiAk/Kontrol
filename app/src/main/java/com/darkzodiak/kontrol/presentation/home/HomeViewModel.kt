package com.darkzodiak.kontrol.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.data.local.dao.ProfileDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileDao: ProfileDao
) : ViewModel() {

    val profiles = profileDao.getProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun onAction(action: HomeAction) {
        when(action) {
            is HomeAction.SwitchProfileState -> {
                viewModelScope.launch {
                    profileDao.upsertProfile(action.profile)
                }
            }
            else -> Unit
        }
    }
}