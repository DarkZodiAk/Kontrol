package com.darkzodiak.kontrol.core.presentation

import androidx.compose.runtime.Composable
import com.darkzodiak.kontrol.profile.domain.ProfileState

@Composable
fun getProfileStateTextInfo(state: ProfileState): String {
    return when (state) {
        ProfileState.Active -> "Активен"
        ProfileState.Stopped -> "Неактивен"
        // TODO(): Make time formatter
        is ProfileState.Paused -> "Пауза, запустится через TODO"
    }
}
