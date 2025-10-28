package com.darkzodiak.kontrol.profile.domain

sealed interface EditRestriction {
    object NoRestriction: EditRestriction
    data class RandomText(val length: Int): EditRestriction {
        companion object { val DEFAULT = RandomText(8) }
    }
    data class Password(val password: String): EditRestriction {
        companion object { val DEFAULT = Password("") }
    }
}