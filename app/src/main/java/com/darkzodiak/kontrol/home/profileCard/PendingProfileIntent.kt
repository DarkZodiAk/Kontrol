package com.darkzodiak.kontrol.home.profileCard

import com.darkzodiak.kontrol.profile.domain.Profile

data class PendingProfileIntent(
    val profile: Profile,
    val intent: ProfileCardIntent
)
