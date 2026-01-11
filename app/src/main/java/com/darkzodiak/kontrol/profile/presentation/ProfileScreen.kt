package com.darkzodiak.kontrol.profile.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.darkzodiak.kontrol.core.presentation.KontrolOutlinedRow
import com.darkzodiak.kontrol.core.presentation.KontrolTextField
import com.darkzodiak.kontrol.core.presentation.unsaved.KontrolUnsavedCard
import com.darkzodiak.kontrol.core.presentation.warning.KontrolWarningCard
import com.darkzodiak.kontrol.profile.presentation.components.AppRestrictionIconText
import com.darkzodiak.kontrol.profile.presentation.components.EditRestrictionIconText

@Composable
fun ProfileScreenRoot(
    viewModel: ProfileViewModel = hiltViewModel(),
    toAppList: () -> Unit,
    toAppRestrictions: () -> Unit,
    toEditRestrictions: () -> Unit,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.render()
        viewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.ShowWarning -> {
                    snackbarHostState.showSnackbar(message = event.text)
                }
                ProfileEvent.GoBack -> onBack()
                ProfileEvent.OpenAppsList -> toAppList()
                ProfileEvent.OpenAppRestriction -> toAppRestrictions()
                ProfileEvent.OpenEditRestriction -> toEditRestrictions()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        ProfileScreen(
            state = viewModel.state,
            onAction = viewModel::onAction
        )
    }
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
                        onClick = { onAction(ProfileAction.GoBack) }
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
            AnimatedVisibility(state.warnings.isNotEmpty()) {
                KontrolWarningCard(state.warnings)
            }
            AnimatedVisibility(state.isNewProfile.not() && state.unsaved) {
                KontrolUnsavedCard()
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
                if (state.apps.isEmpty()) {
                    Text(
                        text = "Выберите приложения",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(state.apps) { app ->
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
                text = "Ограничениe",
                style = MaterialTheme.typography.titleMedium
            )
            KontrolOutlinedRow(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAction(ProfileAction.OpenAppRestriction) }
            ) {
                AppRestrictionIconText(
                    type = state.appRestriction.toType(),
                    data = state.appRestriction,
                    showText = true,
                    showOptionsText = true
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAction(ProfileAction.OpenEditRestriction) }
            ) {
                EditRestrictionIconText(
                    type = state.editRestriction.toType(),
                    data = state.editRestriction,
                    showText = true,
                    showOptionsText = true
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        state = ProfileScreenState(unsaved = true, isNewProfile = false),
        onAction = { }
    )
}