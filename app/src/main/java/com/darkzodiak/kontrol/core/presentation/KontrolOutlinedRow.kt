package com.darkzodiak.kontrol.core.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun KontrolOutlinedRow(
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(16.dp),
    modifier: Modifier = Modifier,
    content: @Composable (RowScope.() -> Unit)
) {
    Row(
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(modifier)
            .border(
                width = 1.dp,
                color = Color(202, 196, 208),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        content()
    }
}

@Preview
@Composable
private fun KontrolOutlinedRowPreview() {
    KontrolOutlinedRow {
        Text("hello")
    }
}