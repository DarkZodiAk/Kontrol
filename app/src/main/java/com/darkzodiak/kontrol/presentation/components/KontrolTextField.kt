package com.darkzodiak.kontrol.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun KontrolTextField(
    text: String,
    placeholder: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = Color(121, 116, 126), shape = RoundedCornerShape(12.dp))
            .padding(start = 16.dp, top = 14.dp, bottom = 14.dp)
    ) {
        BasicTextField(
            value = if(text.isNotBlank() || isFocused) text else placeholder,
            onValueChange = onTextChange,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }

        )
        if(text.isNotBlank()){
            Icon(
                imageVector = Icons.Default.HighlightOff,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { onTextChange("") }
            )
        }
    }
}