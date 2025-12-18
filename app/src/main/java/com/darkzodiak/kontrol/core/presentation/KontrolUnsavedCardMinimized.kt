package com.darkzodiak.kontrol.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun KontrolUnsavedCardMinimized(
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp)
    ) {
        Icon(imageVector = Icons.Outlined.Save, contentDescription = null)
        Text(
            text = "Изменения не сохранены",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview
@Composable
private fun KontrolUnsavedCardMinimizedPreview() {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        KontrolUnsavedCardMinimized(
            modifier = Modifier.padding(16.dp)
        )
    }
}