package com.darkzodiak.kontrol.home.profileCard

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darkzodiak.kontrol.core.presentation.KontrolDropdownMenu
import com.darkzodiak.kontrol.core.presentation.getProfileTextInfo
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.Profile
import com.darkzodiak.kontrol.profile.domain.ProfileState
import java.time.LocalDateTime

@Composable
fun ProfileCard(
    profile: Profile,
    now: LocalDateTime,
    onIntent: (ProfileCardIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownMenuIsVisible by remember { mutableStateOf(false) }
    val dropdownMenuActions = remember(profile.state) {
        buildList {
            if (profile.state is ProfileState.Active) {
                add("Пауза" to ProfileCardIntent.PAUSE)
                add("Выключить" to ProfileCardIntent.STOP)
            } else if (profile.state is ProfileState.Stopped) {
                add("Включить" to ProfileCardIntent.ACTIVATE)
                add("Включить после" to ProfileCardIntent.DELAYED_ACTIVATE)
            } else {
                add("Включить" to ProfileCardIntent.ACTIVATE)
                add("Изменить паузу" to ProfileCardIntent.CHANGE_PAUSE)
                add("Выключить" to ProfileCardIntent.STOP)
            }
            add("Удалить" to ProfileCardIntent.DELETE)
        }.map { it.first to { onIntent(it.second) } }
    }
    val infoTextColor = if (profile.state is ProfileState.Active) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val infoTextBackground = if (profile.state is ProfileState.Active) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(0.8.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onIntent(ProfileCardIntent.OPEN) }
    ) {
        Text(
            text = getProfileTextInfo(profile, now),
            color = infoTextColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight(600),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(infoTextBackground)
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
                text = profile.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight(500),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { dropdownMenuIsVisible = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                if(dropdownMenuIsVisible) {
                    KontrolDropdownMenu(
                        actions = dropdownMenuActions,
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
            profile = Profile(
                name = "Блокировка",
                state = ProfileState.Active,
//                state = ProfileState.Paused(LocalDateTime.now().plusMinutes(7)),
                editRestriction = EditRestriction.UntilDate(date = LocalDateTime.now().plusDays(2), false)
            ),
            now = LocalDateTime.now(),
            onIntent = {  },
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        )
    }
}
