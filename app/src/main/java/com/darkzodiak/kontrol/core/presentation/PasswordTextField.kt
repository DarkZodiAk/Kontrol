package com.darkzodiak.kontrol.core.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PasswordTextField(
    text: String,
    errorMessage: String?,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by rememberSaveable { mutableStateOf(false) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .border(width = 1.dp, color = Color(121, 116, 126), shape = RoundedCornerShape(12.dp))
                .padding(start = 16.dp, top = 14.dp, bottom = 14.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                visualTransformation = if(!visible) PasswordVisualTransformation() else VisualTransformation.None,
                modifier = Modifier.weight(1f)

            )
            Icon(
                imageVector = if(visible) Icons.Default.VisibilityOff else  Icons.Default.Visibility,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { visible = !visible }
            )
        }
        Spacer(Modifier.height(4.dp))
        errorMessage?.let {
            Text(text = errorMessage, color = Color.Red)
        }
    }
}