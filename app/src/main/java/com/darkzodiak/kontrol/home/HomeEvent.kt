package com.darkzodiak.kontrol.home

sealed interface HomeEvent {
    data class OpenProfile(
        val id: Long,
        val inProtectedMode: Boolean = false
    ): HomeEvent
    object NewProfile: HomeEvent

    object OfferOpenProfileInProtectedMode: HomeEvent
    data class ShowError(val text: String): HomeEvent
}