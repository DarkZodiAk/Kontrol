package com.darkzodiak.kontrol.profile.domain.model

data class Profile(
    val id: Long = DEFAULT_ID,
    val name: String = "",
    val state: ProfileState = ProfileState.Stopped,
    val appRestriction: AppRestriction = AppRestriction.SimpleBlock,
    val editRestriction: EditRestriction = EditRestriction.NoRestriction
) {
    companion object {
        const val DEFAULT_ID = -1L
    }
}
