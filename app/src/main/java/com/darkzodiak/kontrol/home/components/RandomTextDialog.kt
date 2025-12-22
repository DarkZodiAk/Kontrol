package com.darkzodiak.kontrol.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darkzodiak.kontrol.core.presentation.KontrolTextField
import com.darkzodiak.kontrol.core.presentation.getRandomAlphaString

@Composable
fun RandomTextDialog(
    textLength: Int,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val target = rememberSaveable { getRandomAlphaString(textLength) }
    var entered by rememberSaveable { mutableStateOf("") }
    var mismatchError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        title = { Text("Введите текст ниже") },
        text = {
            Column {
                Text(target, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                KontrolTextField(
                    text = entered,
                    placeholder = "",
                    onTextChange = {
                        mismatchError = false
                        entered = it
                    }
                )
                if(mismatchError) {
                    Spacer(Modifier.height(4.dp))
                    Text("Текст введен неверно", color = Color.Red)
                }
            }
        },
        onDismissRequest = { },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if(entered == target) onSuccess()
                    else mismatchError = true
                }
            ) {
                Text("Разблокировать")
            }
        },
        modifier = modifier
    )
}