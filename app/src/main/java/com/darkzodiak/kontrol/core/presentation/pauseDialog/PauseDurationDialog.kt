package com.darkzodiak.kontrol.core.presentation.pauseDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties

@Composable
fun PauseDurationDialog(
    onSetPause: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTime by remember {  }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false),
        title = {
            Text(
                text = "Выберите время паузы"
            )
        },
        text = {
            Column {
                AssistChip(
                    onClick = { },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null)
                    },
                    label = {
                        Text(
                            text =
                        )
                    }
                )
            }
        },
        confirmButton = {

        }
    )
}

@Preview
@Composable
private fun PauseDurationDialogPreview() {
    Box(Modifier.fillMaxSize().background(Color.Black))
    PauseDurationDialog(
        onSetPause = {},
        onDismiss = {}
    )
}