package com.darkzodiak.kontrol.core.presentation.delayDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.darkzodiak.kontrol.core.presentation.time.TimeSource
import com.darkzodiak.kontrol.core.presentation.time.toFullString
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime

@Composable
fun SelectDelayTypeDialog(
    initialDelayType: DelayType = DelayType.CUSTOM,
    onSelectDelay: (DelayType) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentTime by rememberSaveable { mutableStateOf(LocalDateTime.now()) }
    var selectedDelayType by rememberSaveable { mutableStateOf(initialDelayType) }

    LaunchedEffect(Unit) {
        TimeSource.currentTime.onEach {
            currentTime = it
        }.launchIn(scope)
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(vertical = 32.dp) // TODO(): Determine padding
                .width(288.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White) // TODO(): Remove background?
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(DelayType.entries) {
                    DelayTypeOption(
                        delayType = it,
                        selected = selectedDelayType == it,
                        onClick = {
                            selectedDelayType = it

                        }
                    )
                }
            }
            if (selectedDelayType != DelayType.CUSTOM) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "Время включения профиля:")
                    Text(text = currentTime.toFullString())
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        TimeSource.setTimeOffset(initialDelayType.delay)
                    }
                ) {
                    Text(text = "Отмена")
                }
                Button(
                    onClick = {
                        onSelectDelay(selectedDelayType)
                        TimeSource.setTimeOffset(selectedDelayType.delay)
                    }
                ) {
                    Text(text = "Применить")
                }
            }
        }
    }
}

@Preview
@Composable
private fun SelectDelayTypeDialogPreview() {
    SelectDelayTypeDialog(
        onSelectDelay = {},
        onDismiss = {}
    )
}