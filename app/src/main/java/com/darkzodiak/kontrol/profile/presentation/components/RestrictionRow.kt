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
import com.darkzodiak.kontrol.core.presentation.time.toFullString
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType

@Composable
fun RestrictionRow(
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
        RestrictionIconText(type, data, active, false)
    }
}

@Composable
fun RestrictionIconText(
    type: EditRestrictionType,
    data: EditRestriction,
    showInfo: Boolean,
    showOptionsInfo: Boolean
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
        text = buildEditRestrictionString(type, data, showInfo, showOptionsInfo),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun buildEditRestrictionString(
    type: EditRestrictionType,
    data: EditRestriction,
    showInfo: Boolean,
    showOptionsInfo: Boolean
) = when (type) {
    EditRestrictionType.NO_RESTRICTION -> "Нет"
    EditRestrictionType.PASSWORD -> {
        val start = "Пароль"
        val end = if (showInfo && data is EditRestriction.Password) {
            " (${data.password.length} символов)"
        } else ""

        start + end
    }
    EditRestrictionType.RANDOM_TEXT -> {
        val start = "Случайный текст"
        val end = if (showInfo && data is EditRestriction.RandomText) {
            " (${data.length} символов)"
        } else ""

        start + end
    }
    EditRestrictionType.UNTIL_DATE -> {
        val start = "До даты"
        val end = if (showInfo && data is EditRestriction.UntilDate) {
            val part1 = " (${data.date.toFullString()})"
            val part2 = if (showOptionsInfo && data.stopAfterReachingDate) {
                "\nВыключится по достижении даты"
            } else ""
            part1 + part2
        }
        else ""

        start + end
    }
    EditRestrictionType.UNTIL_REBOOT -> {
        val start = "До перезагрузки устройства"
        val end = if (showOptionsInfo && data is EditRestriction.UntilReboot && data.stopAfterReboot) {
            "\nВыключится после перезагрузки"
        } else ""

        start + end
    }
}

//) = when (restriction) {
//
//    EditRestriction.NoRestriction -> "Нет"
//    is EditRestriction.Password -> {
//        val start = "Пароль"
//        val end = if (showInfo) " (${restriction.password.length} символов)"
//        else ""
//        start + end
//    }
//    is EditRestriction.RandomText -> {
//        val start = "Случайный текст"
//        val end = if (showInfo) " (${restriction.length} символов)"
//        else ""
//        start + end
//    }
//    is EditRestriction.UntilDate -> {
//        val start = "До даты"
//        val end = if (showInfo) {
//            val part1 = " (${restriction.date.toFullString()})"
//            val part2 = if (showOptionsInfo && restriction.stopAfterReachingDate) {
//                "\nВыключится по достижении даты"
//            } else ""
//            part1 + part2
//        }
//        else ""
//        start + end
//    }
//    is EditRestriction.UntilReboot -> "До перезагрузки устройства"
//}