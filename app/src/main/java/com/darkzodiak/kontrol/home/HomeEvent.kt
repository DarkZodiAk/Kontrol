package com.darkzodiak.kontrol.home

sealed interface HomeEvent {
    data class OpenProfile(val id: Long): HomeEvent
    data class ShowError(val text: String): HomeEvent
}