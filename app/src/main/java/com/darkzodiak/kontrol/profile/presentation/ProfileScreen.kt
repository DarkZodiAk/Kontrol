package com.darkzodiak.kontrol.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.darkzodiak.kontrol.core.presentation.KontrolOutlinedRow
import com.darkzodiak.kontrol.core.presentation.KontrolTextField
import com.darkzodiak.kontrol.profile.presentation.components.RestrictionIconText

@Composable
fun ProfileScreenRoot(
    viewModel: ProfileViewModel = hiltViewModel(),
    toAppList: () -> Unit,
    toRestrictions: () -> Unit,
    onBack: () -> Unit
) {
    ProfileScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                ProfileAction.Back -> onBack()
                ProfileAction.Done -> onBack()
                ProfileAction.OpenAppsList -> toAppList()
                ProfileAction.OpenEditRestriction -> toRestrictions()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileScreenState,
    onAction: (ProfileAction) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(text = if(state.isNewProfile) "Создать профиль" else "Изменить профиль")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(ProfileAction.Back) }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onAction(ProfileAction.Done) },
                        enabled = state.name.isNotBlank()
                    ) {
                        Icon(Icons.Default.Done, null)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            state.warning?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(16.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.ErrorOutline, contentDescription = null)
                    Column {
                        Text(
                            text = "Предупреждение",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = state.warning,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            KontrolTextField(
                text = state.name,
                placeholder = "Название",
                onTextChange = { onAction(ProfileAction.ModifyName(it)) },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Заблокированные приложения",
                style = MaterialTheme.typography.titleMedium
            )
            KontrolOutlinedRow(
                modifier = Modifier.clickable { onAction(ProfileAction.OpenAppsList) }
            ) {
                Icon(imageVector = Icons.Default.Apps, contentDescription = null)
                if (state.selectedApps.isEmpty()) {
                    Text(
                        text = "Выберите приложения",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(state.selectedApps) { app ->
                            AsyncImage(
                                model = app.icon,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
            Text(
                text = "Ограничения",
                style = MaterialTheme.typography.titleMedium
            )
            KontrolOutlinedRow {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Text(
                    text = "Добавить",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Блокировка профиля",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Блокирует редактирование профиля",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            KontrolOutlinedRow(
                modifier = Modifier.clickable { onAction(ProfileAction.OpenEditRestriction) }
            ) {
                RestrictionIconText(state.editRestriction, showDetails = true)
            }
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        state = ProfileScreenState(),
        onAction = { }
    )
}