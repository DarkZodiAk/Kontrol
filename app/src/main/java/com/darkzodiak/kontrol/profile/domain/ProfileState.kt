package com.darkzodiak.kontrol.profile.domain

sealed interface ProfileState {
    object Active: ProfileState
    object Stopped: ProfileState
    data class Paused(val until: Long): ProfileState
}