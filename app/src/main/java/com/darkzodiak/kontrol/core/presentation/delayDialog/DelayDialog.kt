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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.darkzodiak.kontrol.core.presentation.time.DateTimePickerDialog
import com.darkzodiak.kontrol.core.presentation.time.TimeSource
import com.darkzodiak.kontrol.core.presentation.time.toDateString
import com.darkzodiak.kontrol.core.presentation.time.toTimeString
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime

// TODO(): Create ViewModel for such dialogs
@Composable
fun DelayDialog(
    actionDelayType: ActionDelayType,
    onSetPause: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {

    val scope = rememberCoroutineScope()
    var currentTime by rememberSaveable { mutableStateOf(LocalDateTime.now()) }
    var selectedDelayType by rememberSaveable { mutableStateOf(DelayType.CUSTOM) }

    var datePickerVisible by rememberSaveable { mutableStateOf(false) }
    var timePickerVisible by rememberSaveable { mutableStateOf(false) }
    var selectDelayTypeDialogVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        TimeSource.currentTime.onEach {
            currentTime = it
        }.launchIn(scope)
    }

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
                        Text(text = selectedDelayType.text)
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
                            Text(text = currentTime.toDateString())
                        },
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AssistChip(
                        onClick = { datePickerVisible },
                        label = {
                            Text(text = currentTime.toTimeString())
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
            Button(onClick = { onSetPause(currentTime) }) {
                Text(text = "Применить")
            }
        }
    )

    if (datePickerVisible) {
        DateTimePickerDialog(
            isPickingDate = true,
            onDateSelected = { currentTime = it }, //TODO(): ?????
            onDismiss = { datePickerVisible = false }
        )
    }
    if (timePickerVisible) {
        DateTimePickerDialog(
            isPickingDate = false,
            onDateSelected = { currentTime = it }, //TODO(): ?????
            onDismiss = { datePickerVisible = false }
        )
    }

    if (selectDelayTypeDialogVisible) {
        SelectDelayTypeDialog(
            initialDelayType = selectedDelayType,
            onSelectDelay = {
                selectedDelayType = it
                selectDelayTypeDialogVisible = false
            },
            onDismiss = {
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
        actionDelayType = ActionDelayType.ACTIVATE_AFTER,
        onSetPause = {},
        onDismiss = {}
    )
}