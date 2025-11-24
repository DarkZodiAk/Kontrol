package com.darkzodiak.kontrol.core.presentation

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun KontrolDropdownMenu(
    actions: List<Pair<String, () -> Unit>>,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = {
            onDismiss()
        },
    ) {
        actions.forEach { action ->
            DropdownMenuItem(
                onClick = {
                    action.second()
                    onDismiss()
                },
                text = {
                    Text(
                        text = action.first,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
            )
        }
    }
}