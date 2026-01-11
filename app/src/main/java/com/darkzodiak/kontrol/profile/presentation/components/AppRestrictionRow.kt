package com.darkzodiak.kontrol.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darkzodiak.kontrol.core.presentation.KontrolOutlinedRow
import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType
import com.darkzodiak.kontrol.profile.domain.AppRestriction

@Composable
fun AppRestrictionRow(
    type: AppRestrictionType,
    data: AppRestriction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val active = remember(data) { data.isInstanceOf(type) }

    KontrolOutlinedRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .background(
                if (active) MaterialTheme.colorScheme.surfaceContainer
                else Color.Unspecified
            )
            .clickable { onClick() }
    ) {
        AppRestrictionIconText(type, data, active, false)
    }
}

@Composable
fun AppRestrictionIconText(
    type: AppRestrictionType,
    data: AppRestriction,
    showText: Boolean,
    showOptionsText: Boolean,
    hideSensitiveInfo: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = when (type) {
                AppRestrictionType.SIMPLE_BLOCK -> Icons.Outlined.Lock
                AppRestrictionType.PASSWORD -> Icons.Default.Password
                AppRestrictionType.RANDOM_TEXT -> Icons.Default.Password
            },
            contentDescription = null
        )
        Text(
            text = buildAppRestrictionText(type, data, showText, showOptionsText, hideSensitiveInfo),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun buildAppRestrictionText(
    type: AppRestrictionType,
    data: AppRestriction,
    showText: Boolean,
    showOptionsText: Boolean,
    hideSensitiveInfo: Boolean
) = when (type) {
    AppRestrictionType.SIMPLE_BLOCK -> "Простая блокировка"
    AppRestrictionType.PASSWORD -> {
        val start = "Пароль"
        val end = if (showText && hideSensitiveInfo.not() && data is AppRestriction.Password) {
            " (${data.password.length} символов)"
        } else ""

        start + end
    }
    AppRestrictionType.RANDOM_TEXT -> {
        val start = "Случайный текст"
        val end = if (showText && data is AppRestriction.RandomText) {
            " (${data.length} символов)"
        } else ""

        start + end
    }
}