package com.darkzodiak.kontrol.profile.domain

data class Profile(
    val id: Long? = null,
    val name: String = "",
    val state: ProfileState = ProfileState.Stopped,
    val editRestriction: EditRestriction = EditRestriction.NoRestriction
)
