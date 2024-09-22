package com.darkzodiak.kontrol.presentation.home

import com.darkzodiak.kontrol.data.local.entity.Profile

sealed interface HomeAction {
    object NewProfile: HomeAction
    data class OpenProfile(val id: Long): HomeAction
    data class SwitchProfileState(val profile: Profile): HomeAction
}