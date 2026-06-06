package com.darkzodiak.kontrol.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import com.darkzodiak.kontrol.R
import com.darkzodiak.kontrol.core.presentation.time.UITimeUtils
import com.darkzodiak.kontrol.profile.domain.model.EditRestriction
import com.darkzodiak.kontrol.profile.domain.model.Profile
import com.darkzodiak.kontrol.profile.domain.model.ProfileState
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun getProfileTextInfo(profile: Profile, now: LocalDateTime): String {
    return when (profile.state) {
        ProfileState.Active -> getActiveProfileRestrictionInfo(profile.editRestriction, now)
        ProfileState.Stopped -> "Неактивен"

        is ProfileState.Paused -> {
            val duration = Duration.between(now, profile.state.until)
            if (duration.isNegative || duration.isZero) return "Пауза, время истекло"

            val totalSeconds = duration.seconds

            val totalMinutes = (totalSeconds + 59) / 60

            val days = (totalMinutes / 1440).toInt()
            val hours = ((totalMinutes % 1440) / 60).toInt()
            val minutes = (totalMinutes % 60).toInt()

            "Пауза, запустится через " + when {
                days >= 1 -> pluralStringResource(R.plurals.after_days, days, days)
                hours >= 1 -> pluralStringResource(R.plurals.after_hours, hours, hours)
                minutes >= 1 -> pluralStringResource(R.plurals.after_minutes, minutes, minutes)
                else -> pluralStringResource(R.plurals.after_minutes, 1)
            }
        }
    }
}

@Composable
fun getActiveProfileRestrictionInfo(restriction: EditRestriction, now: LocalDateTime): String {
    return when(restriction) {
        is EditRestriction.Password -> "Активен, защищен паролем"
        is EditRestriction.RandomText -> "Активен, защищен случайным текстом"
        is EditRestriction.UntilDate -> {
            val prefix = if (restriction.stopAfterReachingDate) "Активен до " else "Заблокирован до "

            val date = restriction.date
            val currentDate = now.toLocalDate()
            val targetDate = date.toLocalDate()
            prefix + if (targetDate == currentDate) {
                UITimeUtils.formatTime(date)
            }
            else if (targetDate.year == currentDate.year) {
                UITimeUtils.formatDayAndTime(date)
            }
            else UITimeUtils.formatDateTime(date)
        }
        is EditRestriction.UntilReboot -> {
            if (restriction.stopAfterReboot) "Активен до перезагрузки устройства"
            else "Заблокирован до перезагрузки устройства"
        }
        else -> "Активен"
    }
}

fun getRandomAlphaString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') - 'I' - 'l'
    return String(CharArray(length) { allowedChars.random() })
}
