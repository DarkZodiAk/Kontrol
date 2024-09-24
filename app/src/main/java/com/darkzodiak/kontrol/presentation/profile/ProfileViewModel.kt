package com.darkzodiak.kontrol.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.darkzodiak.kontrol.data.local.dao.ProfileDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileDao: ProfileDao
) : ViewModel() {
    var state by mutableStateOf(ProfileState())
        private set

}