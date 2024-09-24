package com.darkzodiak.kontrol.presentation.profile

import com.darkzodiak.kontrol.data.local.entity.Profile

data class ProfileState(
    val profile: Profile = Profile(),
    val name: String = "",
    val selectedApps: List<String> = emptyList()
)
