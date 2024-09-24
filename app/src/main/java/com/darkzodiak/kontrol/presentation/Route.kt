package com.darkzodiak.kontrol.presentation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    object HomeScreen: Route
    @Serializable
    data class ProfileScreen(val id: Long? = null): Route
}