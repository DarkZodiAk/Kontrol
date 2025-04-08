package com.darkzodiak.kontrol.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PasswordDialog(
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var password by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }

    val passwordError = remember(password) {
        if(password.isEmpty()) "Пароль не записан"
        else if(password.length < 8) "Пароль должен быть не менее 8 символов"
        else null
    }
    val repeatPasswordError = remember(password, repeatPassword) {
        if(password != repeatPassword) "Пароли не совпадают"
        else null
    }

    AlertDialog(
        onDismissRequest = { },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Блокирует редактирование профиля, пока не будет записан пароль. Пароль чувствителен к регистру")
                PasswordTextField(
                    text = password,
                    errorMessage = passwordError,
                    onTextChange = { password = it }
                )
                Text("Повторите пароль")
                PasswordTextField(
                    text = repeatPassword,
                    errorMessage = repeatPasswordError,
                    onTextChange = { repeatPassword = it }
                )
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        confirmButton = {
            Button(
                enabled = password.length >= 8 && password == repeatPassword,
                onClick = { onSave(password) }
            ) {
                Text("Сохранить")
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun PasswordDialogPreview() {
    PasswordDialog({},{})
}