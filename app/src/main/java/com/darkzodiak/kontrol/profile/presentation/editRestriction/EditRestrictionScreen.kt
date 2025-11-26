package com.darkzodiak.kontrol.profile.presentation.editRestriction

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkzodiak.kontrol.core.presentation.KontrolOption
import com.darkzodiak.kontrol.core.presentation.delayDialog.DelayDialog
import com.darkzodiak.kontrol.core.presentation.delayDialog.DelayDialogType
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.presentation.components.PasswordDialog
import com.darkzodiak.kontrol.profile.presentation.components.RandomTextDialog
import com.darkzodiak.kontrol.profile.presentation.components.RestrictionRow

@Composable
fun EditRestrictionScreenRoot(
    viewModel: EditRestrictionViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.render()
        viewModel.events.collect { event ->
            when (event) {
                is EditRestrictionEvent.ShowWarning -> {
                    Toast.makeText(context, event.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    EditRestrictionScreen(
        state = viewModel.state,
        onAction = { action ->
            viewModel.onAction(action)
            when(action) {
                EditRestrictionAction.Dismiss -> onBack()
                EditRestrictionAction.Save -> onBack()
                else -> Unit
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRestrictionScreen(
    state: EditRestrictionState,
    onAction: (EditRestrictionAction) -> Unit
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Выберите блокировку") },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(EditRestrictionAction.Dismiss) }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(EditRestrictionAction.Save) } ) {
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
            RestrictionRow(
                type = EditRestrictionType.NO_RESTRICTION,
                data = state.restriction,
                onClick = { onAction(EditRestrictionAction.SetRestriction(EditRestrictionType.NO_RESTRICTION)) }
            )

            RestrictionRow(
                type = EditRestrictionType.PASSWORD,
                data = state.restriction,
                onClick = { onAction(EditRestrictionAction.SetRestriction(EditRestrictionType.PASSWORD)) }
            )

            RestrictionRow(
                type = EditRestrictionType.RANDOM_TEXT,
                data = state.restriction,
                onClick = { onAction(EditRestrictionAction.SetRestriction(EditRestrictionType.RANDOM_TEXT)) }
            )

            RestrictionRow(
                type = EditRestrictionType.UNTIL_DATE,
                data = state.restriction,
                onClick = { onAction(EditRestrictionAction.SetRestriction(EditRestrictionType.UNTIL_DATE)) }
            )

            if (state.restriction is EditRestriction.UntilDate) {
                val restriction = state.restriction
                KontrolOption(
                    checked = restriction.stopAfterReachingDate,
                    text = "Отключить по достижении даты",
                    onClick = {
                        onAction(EditRestrictionAction.SwitchOption(OptionType.STOP_AFTER_DATE))
                    }
                )
            }

            RestrictionRow(
                type = EditRestrictionType.UNTIL_REBOOT,
                data = state.restriction,
                onClick = { onAction(EditRestrictionAction.SetRestriction(EditRestrictionType.UNTIL_REBOOT)) }
            )

            if (state.restriction is EditRestriction.UntilReboot) {
                val restriction = state.restriction
                KontrolOption(
                    checked = restriction.stopAfterReboot,
                    text = "Отключить после перезапуска",
                    onClick = {
                        onAction(EditRestrictionAction.SwitchOption(OptionType.STOP_AFTER_REBOOT))
                    }
                )
            }
        }

        // TODO(): We should somehow deal with restriction changes (user-friendly caching?)

        if (state.openedDialogType == DialogType.PASSWORD) {
            PasswordDialog(
                onSave = {
                    onAction(EditRestrictionAction.SendDialogData(DialogData.Password(it)))
                },
                onDismiss = { onAction(EditRestrictionAction.DismissDialog) },
                oldValue = (state.restriction as? EditRestriction.Password)?.password ?: ""
            )
        }

        if (state.openedDialogType == DialogType.RANDOM_TEXT) {
            RandomTextDialog(
                onSave = {
                    onAction(EditRestrictionAction.SendDialogData(DialogData.RandomText(it)))
                },
                onDismiss = { onAction(EditRestrictionAction.DismissDialog) },
                oldValue = (state.restriction as? EditRestriction.RandomText)?.length
            )
        }

        if (state.openedDialogType == DialogType.UNTIL_DATE) {
            DelayDialog(
                type = DelayDialogType.RESTRICT_UNTIL,
                onSetPause = {
                    onAction(EditRestrictionAction.SendDialogData(DialogData.UntilDate(it)))
                },
                onDismiss = { onAction(EditRestrictionAction.DismissDialog) },
                oldValue = (state.restriction as? EditRestriction.UntilDate)?.date
            )
        }
    }
}

@Preview
@Composable
fun EditRestrictionScreenPreview() {
    EditRestrictionScreen(
        state = EditRestrictionState(),
        onAction = {}
    )
}