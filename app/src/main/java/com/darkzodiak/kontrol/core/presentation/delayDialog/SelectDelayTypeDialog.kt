package com.darkzodiak.kontrol.core.presentation.delayDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.darkzodiak.kontrol.core.presentation.time.UITimeUtils

@Composable
fun SelectDelayTypeDialog(
    state: DelayDialogState,
    type: DelayDialogType,
    onSelectDelay: (DelayType) -> Unit,
    onSaveDelay: () -> Unit,
    onDismiss: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(vertical = 96.dp)
                .width(288.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(DelayType.entries) {
                    DelayTypeOption(
                        delayType = it,
                        selected = state.unsavedDelayType == it,
                        onClick = { onSelectDelay(it) }
                    )
                }
            }
            if (state.unsavedDelayType != DelayType.CUSTOM) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = getDelayInfoText(type))
                    Text(text = UITimeUtils.formatDateTime(state.selectDelayTime))
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onDismiss) {
                    Text(text = "Отмена")
                }
                Button(onClick = onSaveDelay) {
                    Text(text = "Применить")
                }
            }
        }
    }
}

@Composable
fun getDelayInfoText(type: DelayDialogType) = when (type) {
    DelayDialogType.PAUSE -> "Время включения профиля:"
    DelayDialogType.RESTRICT_UNTIL -> "Блокировка профиля до:"
}

@Preview
@Composable
private fun SelectDelayTypeDialogPreview() {
    Box(Modifier
        .fillMaxSize()
        .background(Color.Black))
    SelectDelayTypeDialog(
        state = DelayDialogState(unsavedDelayType = DelayType.HOURS_1),
        type = DelayDialogType.RESTRICT_UNTIL,
        onSelectDelay = {},
        onSaveDelay = {},
        onDismiss = {}
    )
}