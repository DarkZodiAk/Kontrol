package com.darkzodiak.kontrol.core.presentation.warning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun KontrolWarningCard(
    warnings: List<WarningType>,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = Icons.Outlined.WarningAmber, contentDescription = null)
            Text(
                text = "Предупреждение",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(Modifier.height(8.dp))
        for (i in warnings.indices) {
            Text(
                text = "• " + warnings[i].text,
                style = MaterialTheme.typography.bodyMedium
            )
            if (i < warnings.size - 1) Spacer(Modifier.height(4.dp))
        }
    }
}

@Preview
@Composable
private fun KontrolWarningCardPreview() {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        KontrolWarningCard(
            warnings = listOf(
                WarningType.PROFILE_OVERLAP,
                WarningType.LOCKED_EDIT
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}