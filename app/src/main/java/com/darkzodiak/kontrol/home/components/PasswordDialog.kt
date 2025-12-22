package com.darkzodiak.kontrol.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.darkzodiak.kontrol.core.presentation.PasswordTextField

@Composable
fun PasswordDialog(
    password: String,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var entered by rememberSaveable { mutableStateOf("") }
    var mismatchError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        title = { Text("Введите пароль") },
        text = {
            Column {
                PasswordTextField(
                    text = entered,
                    errorMessage = if (mismatchError) "Пароль введен неверно" else null,
                    onTextChange = {
                        mismatchError = false
                        entered = it
                    }
                )
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
                    if(entered == password) onSuccess()
                    else mismatchError = true
                }
            ) {
                Text("Разблокировать")
            }
        },
        modifier = modifier
    )
}