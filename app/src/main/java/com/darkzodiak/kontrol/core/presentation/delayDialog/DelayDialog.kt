package com.darkzodiak.kontrol.core.presentation.delayDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.darkzodiak.kontrol.core.presentation.time.DateTimePickerDialog
import com.darkzodiak.kontrol.core.presentation.time.toDateString
import com.darkzodiak.kontrol.core.presentation.time.toTimeString
import java.time.LocalDateTime

// Main dialog for getting time with delay (for pause, for example)
@Composable
fun DelayDialog(
    viewModel: DelayDialogViewModel = hiltViewModel(),
    actionDelayType: ActionDelayType,
    onSetPause: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    var datePickerVisible by rememberSaveable { mutableStateOf(false) }
    var timePickerVisible by rememberSaveable { mutableStateOf(false) }
    var selectDelayTypeDialogVisible by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false),
        title = {
            Text(
                text = actionDelayType.dialogTitle,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { selectDelayTypeDialogVisible = true },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null)
                    },
                    label = {
                        Text(text = viewModel.state.delayType.text)
                    },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AssistChip(
                        onClick = { datePickerVisible },
                        label = {
                            Text(text = viewModel.state.delayTime.toDateString())
                        },
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AssistChip(
                        onClick = { datePickerVisible },
                        label = {
                            Text(text = viewModel.state.delayTime.toTimeString())
                        },
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        },
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = "Отмена")
            }
        },
        confirmButton = {
            Button(onClick = { onSetPause(viewModel.state.delayTime) }) {
                Text(text = "Применить")
            }
        }
    )

    if (datePickerVisible) {
        DateTimePickerDialog(
            isPickingDate = true,
            onDateSelected = { viewModel.onAction(DelayDialogAction.SetCustomTime(it)) },
            onDismiss = { datePickerVisible = false }
        )
    }
    if (timePickerVisible) {
        DateTimePickerDialog(
            isPickingDate = false,
            onDateSelected = { viewModel.onAction(DelayDialogAction.SetCustomTime(it)) },
            onDismiss = { datePickerVisible = false }
        )
    }

    if (selectDelayTypeDialogVisible) {
        SelectDelayTypeDialog(
            state = viewModel.state,
            onSelectDelay = {
                viewModel.onAction(DelayDialogAction.SelectDelayType(it))
            },
            onSaveDelay = {
                viewModel.onAction(DelayDialogAction.SaveDelayType)
                selectDelayTypeDialogVisible = false
            },
            onDismiss = {
                viewModel.onAction(DelayDialogAction.DismissDelayType)
                selectDelayTypeDialogVisible = false
            }
        )
    }
}

@Preview
@Composable
private fun DelayDialogPreview() {
    Box(Modifier
        .fillMaxSize()
        .background(Color.Black))
    DelayDialog(
        actionDelayType = ActionDelayType.PAUSE,
        onSetPause = {},
        onDismiss = {}
    )
}