package com.darkzodiak.kontrol.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProfileCard(
    infoText: String,
    title: String,
    isActive: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
    onActivate: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownMenuIsVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(0.8.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onClick() }
    ) {
        Text(
            text = infoText,
            color = if(isActive) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight(600),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if(isActive) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceContainerHighest
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight(500),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { dropdownMenuIsVisible = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                if(dropdownMenuIsVisible) {
                    KontrolDropdownMenu(
                        actions = hashMapOf(
                            "Удалить" to onDelete
                        ).apply {
                            if(isActive) {
                                put("Пауза", onPause)
                                put("Выключить", onStop)
                            } else {
                                put("Включить", onActivate)
                            }
                        },
                        isLocked = isLocked,
                        onDismiss = { dropdownMenuIsVisible = false }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProfileCardPreview() {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        ProfileCard(
            infoText = "Активно",
            title = "Блокировка",
            isActive = true,
            isLocked = false,
            onClick = {  },
            onActivate = {  },
            onPause = {  },
            onStop = {  },
            onDelete = {  },
            modifier = Modifier
                .padding(it)
                .padding(16.dp)

        )
    }
}
