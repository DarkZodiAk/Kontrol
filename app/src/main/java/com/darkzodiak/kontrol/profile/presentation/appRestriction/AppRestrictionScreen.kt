package com.darkzodiak.kontrol.profile.presentation.appRestriction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkzodiak.kontrol.core.presentation.KontrolUnsavedCard
import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType
import com.darkzodiak.kontrol.profile.domain.AppRestriction
import com.darkzodiak.kontrol.profile.presentation.components.AppRestrictionRow
import com.darkzodiak.kontrol.profile.presentation.components.PasswordDialog
import com.darkzodiak.kontrol.profile.presentation.components.RandomTextDialog

@Composable
fun AppRestrictionScreenRoot(
    viewModel: AppRestrictionViewModel = viewModel(),
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.render()
    }

    AppRestrictionScreen(
        state = viewModel.state,
        onAction = { action ->
            viewModel.onAction(action)
            when(action) {
                AppRestrictionAction.Dismiss -> onBack()
                AppRestrictionAction.Save -> onBack()
                else -> Unit
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRestrictionScreen(
    state: AppRestrictionState,
    onAction: (AppRestrictionAction) -> Unit
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Выберите ограничение") },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(AppRestrictionAction.Dismiss) }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(AppRestrictionAction.Save) } ) {
                        Icon(Icons.Default.Done, contentDescription = null)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            AnimatedVisibility(state.unsaved) {
                KontrolUnsavedCard()
            }
            AppRestrictionType.entries.forEach { type ->
                AppRestrictionRow(
                    type = type,
                    data = state.restriction,
                    onClick = { onAction(AppRestrictionAction.SetRestriction(type)) },
//                    onInfo = { onAction(AppRestrictionAction.ShowInfo(type)) }
                )
            }
        }

        // TODO(): We should somehow deal with restriction changes (user-friendly caching?)

        if (state.openedDialogType == DialogType.PASSWORD) {
            PasswordDialog(
                onSave = {
                    onAction(AppRestrictionAction.SendDialogData(DialogData.Password(it)))
                },
                onDismiss = { onAction(AppRestrictionAction.DismissDialog) },
                oldValue = (state.restriction as? AppRestriction.Password)?.password ?: ""
            )
        }

        if (state.openedDialogType == DialogType.RANDOM_TEXT) {
            RandomTextDialog(
                onSave = {
                    onAction(AppRestrictionAction.SendDialogData(DialogData.RandomText(it)))
                },
                onDismiss = { onAction(AppRestrictionAction.DismissDialog) },
                oldValue = (state.restriction as? AppRestriction.RandomText)?.length
            )
        }
    }
}

@Preview
@Composable
fun AppRestrictionScreenPreview() {
    AppRestrictionScreen(
        state = AppRestrictionState(unsaved = true),
        onAction = {}
    )
}