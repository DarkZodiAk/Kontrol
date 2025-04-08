package com.darkzodiak.kontrol.domain.model

sealed interface EditRestriction {
    object NoRestriction: EditRestriction
    data class RandomPassword(val length: Int): EditRestriction {
        companion object { val DEFAULT = RandomPassword(16) }
    }
    data class Password(val password: String): EditRestriction {
        companion object { val DEFAULT = Password("") }
    }
}