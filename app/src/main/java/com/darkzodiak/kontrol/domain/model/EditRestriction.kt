package com.darkzodiak.kontrol.domain.model

sealed interface EditRestriction {
    object NoRestriction: EditRestriction
    data class RandomPassword(val length: Int): EditRestriction
    data class Password(val password: String): EditRestriction
}