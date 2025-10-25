package com.darkzodiak.kontrol.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    object HomeScreen: Route

    @Serializable
    object Profile: Route
    @Serializable
    data class ProfileScreen(val id: Long? = null): Route
    @Serializable
    object AppListScreen: Route
    @Serializable
    object EditRestrictionScreen: Route
}