package com.darkzodiak.kontrol.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.darkzodiak.kontrol.domain.model.EditRestriction
import com.darkzodiak.kontrol.presentation.components.KontrolOutlinedRow

@Composable
fun RestrictionRow(
    restriction: EditRestriction,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KontrolOutlinedRow(
        modifier = modifier
            .background(
                if (active) MaterialTheme.colorScheme.surfaceContainer
                else Color.Unspecified
            )
            .clickable { onClick() }
    ) {
        RestrictionIconText(restriction, active)
    }
}

@Composable
fun RestrictionIconText(
    restriction: EditRestriction,
    showDetails: Boolean
) {
    Icon(
        imageVector = when(restriction) {
            EditRestriction.NoRestriction -> Icons.Default.LockOpen
            is EditRestriction.Password -> Icons.Default.Password
            is EditRestriction.RandomPassword -> Icons.Default.Password
        },
        contentDescription = null
    )
    Text(
        text = when(restriction) {
            EditRestriction.NoRestriction -> "Нет"
            is EditRestriction.Password -> "Пароль" + if(showDetails) " (${restriction.password.length} символов)" else ""
            is EditRestriction.RandomPassword -> "Случайный пароль" + if(showDetails) " (${restriction.length} символов)" else ""
        },
        style = MaterialTheme.typography.titleMedium
    )
}