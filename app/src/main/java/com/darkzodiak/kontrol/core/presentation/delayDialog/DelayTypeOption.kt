package com.darkzodiak.kontrol.core.presentation.delayDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DelayTypeOption(
    delayType: DelayType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundModifier = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest)
    } else {
        Modifier
    }

    Text(
        text = delayType.text,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    )
}

@Preview
@Composable
private fun DelayTypeOptionPreview() {
    DelayTypeOption(DelayType.MINUTES_10, true, {})
}