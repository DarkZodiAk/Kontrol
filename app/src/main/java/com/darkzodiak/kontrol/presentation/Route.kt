package com.darkzodiak.kontrol.presentation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    object MainScreen: Route
    @Serializable
    object ProfileScreen: Route
}