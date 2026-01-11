package com.darkzodiak.kontrol.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    object HomeScreen: Route
    @Serializable
    data class ProfileScreen(
        val id: Long? = null,
        val protectedMode: Boolean = false
    ): Route
    @Serializable
    object AppListScreen: Route
    @Serializable
    object AppRestrictionScreen: Route
    @Serializable
    object EditRestrictionScreen: Route
}