package com.darkzodiak.kontrol.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import com.darkzodiak.kontrol.R
import com.darkzodiak.kontrol.profile.domain.ProfileState
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun getProfileStateTextInfo(state: ProfileState, now: LocalDateTime): String {
    return when (state) {
        ProfileState.Active -> "Активен"
        ProfileState.Stopped -> "Неактивен"

        // TODO(): Should be locale-universal in future
        // TODO(): Change/Shorten pause text format?
        is ProfileState.Paused -> {
            val duration = Duration.between(now, state.until)

            val days = duration.toDays().toInt()
            val hours = duration.toHours().toInt()
            val minutes = duration.toMinutes().toInt()

            if (duration.isNegative || duration.isZero) "Пауза, время истекло"
            else "Пауза, запустится через " + if (days >= 1) {
                pluralStringResource(R.plurals.after_days, days, days)
            } else if (hours >= 1) {
                pluralStringResource(R.plurals.after_hours, hours, hours)
            } else if (minutes >= 1) {
                pluralStringResource(R.plurals.after_minutes, minutes, minutes)
            } else pluralStringResource(R.plurals.after_minutes, 1)
        }
    }
}

fun getRandomAlphaString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z')
    return String(CharArray(length) { allowedChars.random() })
}
