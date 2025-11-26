package com.darkzodiak.kontrol.profile.domain

data class Profile(
    val id: Long? = null,
    val name: String = "",
    val state: ProfileState = ProfileState.Stopped,
    val appRestriction: AppRestriction = AppRestriction.SimpleBlock,
    val editRestriction: EditRestriction = EditRestriction.NoRestriction
)
