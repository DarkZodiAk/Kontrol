package com.darkzodiak.kontrol.presentation.profile

data class ProfileState(
    val name: String = "",
    val apps: List<String> = emptyList(),
    val selectedApps: List<String> = emptyList()
)
