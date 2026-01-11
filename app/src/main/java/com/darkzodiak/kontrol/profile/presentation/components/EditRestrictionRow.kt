package com.darkzodiak.kontrol.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.core.presentation.KontrolOutlinedRow
import com.darkzodiak.kontrol.core.presentation.time.UITimeUtils
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType

@Composable
fun EditRestrictionRow(
    type: EditRestrictionType,
    data: EditRestriction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val active = remember(data) { data.isInstanceOf(type) }

    KontrolOutlinedRow(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                if (active) MaterialTheme.colorScheme.surfaceContainer
                else Color.Unspecified
            )
            .clickable { onClick() }
    ) {
        EditRestrictionIconText(type, data, active, false)
    }
}

@Composable
fun EditRestrictionIconText(
    type: EditRestrictionType,
    data: EditRestriction,
    showText: Boolean,
    showOptionsText: Boolean,
    hideSensitiveInfo: Boolean = false
) {
    Icon(
        imageVector = when (type) {
            EditRestrictionType.NO_RESTRICTION -> Icons.Default.LockOpen
            EditRestrictionType.PASSWORD -> Icons.Default.Password
            EditRestrictionType.RANDOM_TEXT -> Icons.Default.Password
            EditRestrictionType.UNTIL_DATE -> Icons.Default.Alarm
            EditRestrictionType.UNTIL_REBOOT -> Icons.Default.RestartAlt
        },
        contentDescription = null
    )
    Text(
        text = buildEditRestrictionText(type, data, showText, showOptionsText, hideSensitiveInfo),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun buildEditRestrictionText(
    type: EditRestrictionType,
    data: EditRestriction,
    showText: Boolean,
    showOptionsText: Boolean,
    hideSensitiveInfo: Boolean
) = when (type) {
    EditRestrictionType.NO_RESTRICTION -> "Нет"
    EditRestrictionType.PASSWORD -> {
        val start = "Пароль"
        val end = if (showText && hideSensitiveInfo.not() && data is EditRestriction.Password) {
            " (${data.password.length} символов)"
        } else ""

        start + end
    }
    EditRestrictionType.RANDOM_TEXT -> {
        val start = "Случайный текст"
        val end = if (showText && data is EditRestriction.RandomText) {
            " (${data.length} символов)"
        } else ""

        start + end
    }
    EditRestrictionType.UNTIL_DATE -> {
        val start = "До даты"
        val end = if (showText && data is EditRestriction.UntilDate) {
            val part1 = " (${UITimeUtils.formatDateTime(data.date)})"
            val part2 = if (showOptionsText && data.stopAfterReachingDate) {
                "\nВыключится по достижении даты"
            } else ""
            part1 + part2
        }
        else ""

        start + end
    }
    EditRestrictionType.UNTIL_REBOOT -> {
        val start = "До перезагрузки устройства"
        val end = if (showOptionsText && data is EditRestriction.UntilReboot && data.stopAfterReboot) {
            "\nВыключится после перезагрузки"
        } else ""

        start + end
    }
}