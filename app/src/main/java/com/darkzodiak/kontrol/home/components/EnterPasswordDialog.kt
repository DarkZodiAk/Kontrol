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
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.core.presentation.KontrolTextField

@Composable
fun EnterPasswordDialog(
    passRestriction: EditRestriction,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetPassword by rememberSaveable {
        mutableStateOf(
            when(passRestriction) {
                is EditRestriction.Password -> passRestriction.password
                is EditRestriction.RandomPassword -> getRandomAlphaString(passRestriction.length)
                else -> throw IllegalArgumentException("Passed non-password restriction")
            }
        )
    }
    var password by rememberSaveable { mutableStateOf("") }
    var mismatchError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        title = { Text("Введите пароль") },
        text = {
            Column {
                if(passRestriction is EditRestriction.RandomPassword) {
                    Text(targetPassword, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                }
                KontrolTextField(
                    text = password,
                    placeholder = "",
                    onTextChange = {
                        mismatchError = false
                        password = it
                    }
                )
                if(mismatchError) {
                    Spacer(Modifier.height(4.dp))
                    Text("Пароль введен неверно", color = Color.Red)
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
                    if(password == targetPassword) onSuccess()
                    else mismatchError = true
                }
            ) {
                Text("Разблокировать")
            }
        },
        modifier = modifier
    )
}

fun getRandomAlphaString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z')
    return String(CharArray(length) { allowedChars.random() })
}