package com.darkzodiak.kontrol.core.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun KontrolOption(
    checked: Boolean,
    text: String,
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(modifier)
            .border(
                width = 1.dp,
                color = Color(121, 116, 126), //Color(202, 196, 208),
                shape = RoundedCornerShape(12.dp)
            ).clickable(onClick = { onClick(checked.not()) })
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onClick
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}