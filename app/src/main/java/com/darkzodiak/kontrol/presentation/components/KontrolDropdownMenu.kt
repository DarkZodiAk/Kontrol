package com.darkzodiak.kontrol.presentation.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun KontrolDropdownMenu(
    actions: HashMap<String, () -> Unit>,
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
                    action.value()
                    onDismiss()
                },
                text = {
                    Text(
                        text = action.key,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
            )
        }
    }
}