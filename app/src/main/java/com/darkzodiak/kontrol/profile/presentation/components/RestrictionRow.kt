package com.darkzodiak.kontrol.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.core.presentation.KontrolOutlinedRow
import com.darkzodiak.kontrol.core.presentation.time.toFullString

@Composable
fun RestrictionRow(
    restriction: EditRestriction,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KontrolOutlinedRow(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                if (active) MaterialTheme.colorScheme.surfaceContainer
                else Color.Unspecified
            )
            .clickable { onClick() }
    ) {
        RestrictionIconText(restriction, active, false)
    }
}

@Composable
fun RestrictionIconText(
    restriction: EditRestriction,
    showInfo: Boolean,
    showOptionsInfo: Boolean
) {
    Icon(
        imageVector = when (restriction) {
            EditRestriction.NoRestriction -> Icons.Default.LockOpen
            is EditRestriction.Password -> Icons.Default.Password
            is EditRestriction.RandomText -> Icons.Default.Password
            is EditRestriction.UntilDate -> Icons.Default.Alarm
        },
        contentDescription = null
    )
    Text(
        text = buildEditRestrictionString(restriction, showInfo, showOptionsInfo),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun buildEditRestrictionString(
    restriction: EditRestriction,
    showInfo: Boolean,
    showOptionsInfo: Boolean
) = when (restriction) {
    EditRestriction.NoRestriction -> "Нет"
    is EditRestriction.Password -> {
        val start = "Пароль"
        val end = if (showInfo) " (${restriction.password.length} символов)"
        else ""
        start + end
    }
    is EditRestriction.RandomText -> {
        val start = "Случайный пароль"
        val end = if (showInfo) " (${restriction.length} символов)"
        else ""
        start + end
    }
    is EditRestriction.UntilDate -> {
        val start = "Блокировка до даты"
        val end = if (showInfo) {
            val part1 = "\n${restriction.date.toFullString()}"
            val part2 = if (showOptionsInfo && restriction.stopAfterReachingDate) {
                "\nВыключится по достижении даты"
            } else ""
            part1 + part2
        }
        else ""
        start + end
    }
}