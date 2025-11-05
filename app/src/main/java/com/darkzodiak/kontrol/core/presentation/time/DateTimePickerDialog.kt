package com.darkzodiak.kontrol.core.presentation.time

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    isPickingDate: Boolean,
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(is24Hour = true)

    val confirmEnabled by remember { derivedStateOf { datePickerState.selectedDateMillis != null} }

    // TODO(): Make ability to move from selecting date to time inside dialog?
    DatePickerDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = "Отмена")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val localTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        val selectedDateTime = LocalDateTime.of(localDate, localTime)
                        onDateSelected(selectedDateTime)
                    }
                },
                enabled = confirmEnabled
            ) {
                Text(text = "Применить")
            }
        }
    ) {
        if(isPickingDate)
            DatePicker(state = datePickerState)
        else
            TimePicker(
                state = timePickerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
    }
}

@Preview
@Composable
private fun DateTimePickerDialogPreview() {
    DateTimePickerDialog(
        isPickingDate = false,
        onDateSelected = {},
        onDismiss = {}
    )
}