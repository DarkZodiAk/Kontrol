package com.darkzodiak.kontrol.profile.domain

data class Profile(
    val id: Long? = null,
    val name: String = "",
    val isEnabled: Boolean = false,
    val editRestriction: EditRestriction = EditRestriction.NoRestriction
)
