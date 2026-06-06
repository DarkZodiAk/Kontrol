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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkzodiak.kontrol.core.presentation.time.DatePickerDialog
import com.darkzodiak.kontrol.core.presentation.time.TimePickerDialog
import com.darkzodiak.kontrol.core.presentation.time.UITimeUtils
import java.time.LocalDateTime

// Main dialog for getting time with delay (for pause, for example)
@Composable
fun DelayDialog(
    viewModel: DelayDialogViewModel = viewModel(),
    type: DelayDialogType,
    onSetPause: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit,
    oldValue: LocalDateTime?
) {
    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.setInitialData(oldValue)
    }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
            viewModel.onAction(DelayDialogAction.Close)
        },
        properties = DialogProperties(dismissOnClickOutside = false),
        title = {
            Text(
                text = getDelayDialogTitle(type),
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { viewModel.onAction(DelayDialogAction.OpenDelayTypePicker) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null)
                    },
                    label = {
                        Text(text = state.delayType.text)
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
                        onClick = { viewModel.onAction(DelayDialogAction.OpenDatePicker) },
                        label = {
                            Text(text = UITimeUtils.formatDate(state.delayTime))
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AssistChip(
                        onClick = { viewModel.onAction(DelayDialogAction.OpenTimePicker) },
                        label = {
                            Text(text = UITimeUtils.formatTime(state.delayTime))
                        },
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    onDismiss()
                    viewModel.onAction(DelayDialogAction.Close)
                }
            ) {
                Text(text = "Отмена")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSetPause(state.delayTime)
                    viewModel.onAction(DelayDialogAction.Close)
                }
            ) {
                Text(text = "Применить")
            }
        }
    )

    if (state.datePickerVisible) {
        DatePickerDialog(
            initialDateTime = state.delayTime,
            onDateSelected = {
                viewModel.onAction(DelayDialogAction.SaveDateTime(it))
            },
            onDismiss = { viewModel.onAction(DelayDialogAction.DismissDatePicker) }
        )
    }
    if (state.timePickerVisible) {
        TimePickerDialog(
            initialDateTime = state.delayTime,
            onDateSelected = {
                viewModel.onAction(DelayDialogAction.SaveDateTime(it))
            },
            onDismiss = { viewModel.onAction(DelayDialogAction.DismissTimePicker) }
        )
    }

    if (state.delayTypePickerVisible) {
        DelayTypePicker(
            state = state,
            type = type,
            onSelectDelay = {
                viewModel.onAction(DelayDialogAction.SelectDelayType(it))
            },
            onSaveDelay = {
                viewModel.onAction(DelayDialogAction.SaveDelayType)
            },
            onDismiss = {
                viewModel.onAction(DelayDialogAction.DismissDelayTypeDialog)
            }
        )
    }
}

@Composable
fun getDelayDialogTitle(type: DelayDialogType) = when (type) {
    DelayDialogType.PAUSE -> "Выберите время включения"
    DelayDialogType.RESTRICT_UNTIL -> "Блокировать профиль до"
}

@Preview
@Composable
private fun DelayDialogPreview() {
    Box(Modifier
        .fillMaxSize()
        .background(Color.Black))
    DelayDialog(
        type = DelayDialogType.RESTRICT_UNTIL,
        onSetPause = {},
        onDismiss = {},
        oldValue = null
    )
}