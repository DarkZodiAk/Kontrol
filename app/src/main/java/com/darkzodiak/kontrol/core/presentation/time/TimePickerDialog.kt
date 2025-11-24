package com.darkzodiak.kontrol.core.presentation.time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialDateTime: LocalDateTime,
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        TimeSource().currentTime.collect { now = it }
    }

    val isToday = rememberSaveable { initialDateTime.toLocalDate() == LocalDate.now() }
    val timePickerState = rememberTimePickerState(
        is24Hour = true,
        initialHour = initialDateTime.hour,
        initialMinute = initialDateTime.minute
    )

    val confirmEnabled by remember {
        derivedStateOf {
            val selectedMinutes = timePickerState.hour * 60 + timePickerState.minute
            val nowMinutes = now.hour * 60 + now.minute
            isToday.not() || selectedMinutes > nowMinutes
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Выберите время",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (confirmEnabled.not()) {
                    Text(
                        text = "Выбранное время должно быть хотя бы на минуту позже текущего",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(text = "Отмена")
                    }

                    Button(
                        onClick = {
                            val localTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                            val selectedDateTime = LocalDateTime.of(initialDateTime.toLocalDate(), localTime)
                            onDateSelected(selectedDateTime)
                        },
                        enabled = confirmEnabled
                    ) {
                        Text(text = "Применить")
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun TimePickerDialogPreview() {
    Box(Modifier
        .fillMaxSize()
        .background(Color.Black))
    TimePickerDialog(
        initialDateTime = LocalDateTime.now(),
        onDateSelected = {},
        onDismiss = {}
    )
}