package com.darkzodiak.kontrol.core.presentation.unsaved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun KontrolUnsavedCard(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Icon(imageVector = Icons.Outlined.Save, contentDescription = null)
        Column {
            Text(
                text = "Изменения не сохранены",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Нажмите на кнопку в верхней правой части экрана, чтобы сохранить изменения",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
private fun KontrolUnsavedCardPreview() {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        KontrolUnsavedCard(modifier = Modifier.padding(16.dp))
    }
}