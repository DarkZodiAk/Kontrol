package com.darkzodiak.kontrol.profile.presentation

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.presentation.components.NoRestrictionDialog
import com.darkzodiak.kontrol.profile.presentation.components.PasswordDialog
import com.darkzodiak.kontrol.profile.presentation.components.RandomPasswordDialog
import com.darkzodiak.kontrol.profile.presentation.components.RestrictionRow

@Composable
fun EditRestrictionScreenRoot(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    EditRestrictionScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                ProfileAction.Restriction.Dismiss -> onBack()
                ProfileAction.Restriction.Save -> onBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRestrictionScreen(
    state: ProfileState,
    onAction: (ProfileAction.Restriction) -> Unit
) {
    var noRestrictionDialogVisible by rememberSaveable { mutableStateOf(false) }
    var passwordDialogVisible by rememberSaveable { mutableStateOf(false) }
    var randomPasswordDialogVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Выберите блокировку") },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(ProfileAction.Restriction.Dismiss) }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(ProfileAction.Restriction.Save) } ) {
                        Icon(Icons.Default.Done, contentDescription = null)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        //Column with Restrictions
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            RestrictionRow(
                restriction = EditRestriction.NoRestriction,
                active = state.editRestrictionUnsaved is EditRestriction.NoRestriction,
                onClick = {
                    if(state.editRestrictionUnsaved !is EditRestriction.NoRestriction) {
                        noRestrictionDialogVisible = true
                    }
                }
            )

            RestrictionRow(
                restriction = getPasswordRestrictionOrDefault(state.editRestrictionUnsaved),
                active = state.editRestrictionUnsaved is EditRestriction.Password,
                onClick = { passwordDialogVisible = true }
            )

            RestrictionRow(
                restriction = getRandPasswordRestrictionOrDefault(state.editRestrictionUnsaved),
                active = state.editRestrictionUnsaved is EditRestriction.RandomText,
                onClick = { randomPasswordDialogVisible = true }
            )
        }

        if(noRestrictionDialogVisible) {
            NoRestrictionDialog(
                onConfirm = {
                    onAction(ProfileAction.Restriction.UpdateType(EditRestriction.NoRestriction))
                },
                onDismiss = { noRestrictionDialogVisible = false }
            )
        }

        if(passwordDialogVisible) {
            PasswordDialog(
                onSave = {
                    onAction(ProfileAction.Restriction.UpdateType(EditRestriction.Password(it)))
                    passwordDialogVisible = false
                },
                onDismiss = { passwordDialogVisible = false },
                oldPassword = getPasswordRestrictionOrDefault(state.editRestrictionUnsaved).password
            )
        }

        if(randomPasswordDialogVisible) {
            RandomPasswordDialog(
                onSave = {
                    onAction(ProfileAction.Restriction.UpdateType(EditRestriction.RandomText(it)))
                    randomPasswordDialogVisible = false
                },
                onDismiss = { randomPasswordDialogVisible = false },
                oldLength = getRandPasswordRestrictionOrDefault(state.editRestrictionUnsaved).length.toString()
            )
        }
    }
}

fun getPasswordRestrictionOrDefault(restriction: EditRestriction): EditRestriction.Password {
    return if(restriction is EditRestriction.Password) restriction
    else EditRestriction.Password.DEFAULT
}

fun getRandPasswordRestrictionOrDefault(restriction: EditRestriction): EditRestriction.RandomText {
    return if(restriction is EditRestriction.RandomText) restriction
    else EditRestriction.RandomText.DEFAULT
}


@Preview
@Composable
fun EditRestrictionScreenPreview() {
    EditRestrictionScreen(
        state = ProfileState(),
        onAction = {}
    )
}