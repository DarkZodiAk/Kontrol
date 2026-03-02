package com.darkzodiak.kontrol.profile.domain.model

import java.time.LocalDateTime

sealed interface ProfileState {
    object Active: ProfileState
    object Stopped: ProfileState
    data class Paused(val until: LocalDateTime): ProfileState
}