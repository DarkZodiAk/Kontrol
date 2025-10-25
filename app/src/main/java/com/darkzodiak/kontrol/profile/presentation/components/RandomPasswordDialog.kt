package com.darkzodiak.kontrol.profile.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RandomPasswordDialog(
    onSave: (Int) -> Unit,
    onDismiss: () -> Unit,
    oldLength: String,
    modifier: Modifier = Modifier
) {
    var length by rememberSaveable { mutableStateOf(oldLength) }

    val errorMessage = remember(length) {
        if(length.isEmpty() || length.toInt() < 4) "Длина пароля должна быть не менее 4 символов"
        else if(length.toInt() > 500) "Длина пароля должна быть не более 500 символов"
        else null
    }

    AlertDialog(
        onDismissRequest = { },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Блокирует редактирование профиля, пока не будет записано")
                NumberTextField(
                    text = length,
                    onTextChange = { length = it.take(4) },
                )
                Text("случайных символов")
                errorMessage?.let {
                    Text(errorMessage, color = Color.Red)
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        confirmButton = {
            Button(
                enabled = errorMessage == null,
                onClick = { onSave(length.toInt()) }
            ) {
                Text("Сохранить")
            }
        },
        modifier = modifier
    )
}

@Composable
fun NumberTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = Color(121, 116, 126), shape = RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun RandomPasswordDialogPreview() {
    RandomPasswordDialog({}, {}, "")
}