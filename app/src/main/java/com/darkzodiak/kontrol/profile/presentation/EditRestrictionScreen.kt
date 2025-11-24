package com.darkzodiak.kontrol.profile.presentation

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darkzodiak.kontrol.core.presentation.KontrolOption
import com.darkzodiak.kontrol.core.presentation.delayDialog.DelayDialog
import com.darkzodiak.kontrol.core.presentation.delayDialog.DelayDialogType
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.presentation.components.PasswordDialog
import com.darkzodiak.kontrol.profile.presentation.components.RandomPasswordDialog
import com.darkzodiak.kontrol.profile.presentation.components.RestrictionRow
import java.time.LocalDateTime

@Composable
fun EditRestrictionScreenRoot(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.ShowWarningOnRestriction -> {
                    Toast.makeText(context, event.text, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

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
    state: ProfileScreenState,
    onAction: (ProfileAction.Restriction) -> Unit
) {
//    var restrictionDataLossDialogVisible by rememberSaveable { mutableStateOf(false) }
    var passwordDialogVisible by rememberSaveable { mutableStateOf(false) }
    var randomTextDialogVisible by rememberSaveable { mutableStateOf(false) }
    var untilDateDialogVisible by rememberSaveable { mutableStateOf(false) }

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
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            RestrictionRow(
                type = EditRestrictionType.NO_RESTRICTION,
                data = state.editRestrictionUnsaved,
                onClick = { onAction(ProfileAction.Restriction.UpdateType(EditRestriction.NoRestriction)) }
            )

            RestrictionRow(
                type = EditRestrictionType.PASSWORD,
                data = state.editRestrictionUnsaved,
                onClick = { passwordDialogVisible = true }
            )

            RestrictionRow(
                type = EditRestrictionType.RANDOM_TEXT,
                data = state.editRestrictionUnsaved,
                onClick = { randomTextDialogVisible = true }
            )

            RestrictionRow(
                type = EditRestrictionType.UNTIL_DATE,
                data = state.editRestrictionUnsaved,
                onClick = { untilDateDialogVisible = true }
            )

            if (state.editRestrictionUnsaved is EditRestriction.UntilDate) {
                val restriction = state.editRestrictionUnsaved
                KontrolOption(
                    checked = restriction.stopAfterReachingDate,
                    text = "Отключить по достижении даты",
                    onClick = {
                        onAction(ProfileAction.Restriction.UpdateType(
                            restriction.copy(stopAfterReachingDate = it)
                        ))
                    }
                )
            }

            RestrictionRow(
                type = EditRestrictionType.UNTIL_REBOOT,
                data = state.editRestrictionUnsaved,
                // TODO(): Actually a bummer. Will be fixed on ProfileViewModel separation
                onClick = { onAction(ProfileAction.Restriction.UpdateType(EditRestriction.UntilReboot(false))) }
            )

            if (state.editRestrictionUnsaved is EditRestriction.UntilReboot) {
                val restriction = state.editRestrictionUnsaved
                KontrolOption(
                    checked = restriction.stopAfterReboot,
                    text = "Отключить после перезапуска",
                    onClick = {
                        onAction(ProfileAction.Restriction.UpdateType(
                            restriction.copy(stopAfterReboot = true)
                        ))
                    }
                )
            }
        }

        // TODO(): We should somehow deal with restriction changes (user-friendly caching?)
//        if (restrictionDataLossDialogVisible) {
//            NoRestrictionDialog(
//                onConfirm = {
//                    onAction(ProfileAction.Restriction.UpdateType(EditRestriction.NoRestriction))
//                },
//                onDismiss = { restrictionDataLossDialogVisible = false }
//            )
//        }

        if (passwordDialogVisible) {
            PasswordDialog(
                onSave = {
                    onAction(ProfileAction.Restriction.UpdateType(EditRestriction.Password(it)))
                    passwordDialogVisible = false
                },
                onDismiss = { passwordDialogVisible = false },
                oldValue = (state.editRestrictionUnsaved as? EditRestriction.Password)?.password ?: ""
            )
        }

        if (randomTextDialogVisible) {
            RandomPasswordDialog(
                onSave = {
                    onAction(ProfileAction.Restriction.UpdateType(EditRestriction.RandomText(it)))
                    randomTextDialogVisible = false
                },
                onDismiss = { randomTextDialogVisible = false },
                // TODO(): Change this behavior when splitting ProfileViewModel
                oldValue = (state.editRestrictionUnsaved as? EditRestriction.RandomText)?.length.toString()
            )
        }

        if (untilDateDialogVisible) {
            DelayDialog(
                type = DelayDialogType.RESTRICT_UNTIL,
                onSetPause = {
                    val restriction = state.editRestrictionUnsaved
                    onAction(ProfileAction.Restriction.UpdateType(
                        EditRestriction.UntilDate(
                            date = it,
                            stopAfterReachingDate = restriction is EditRestriction.UntilDate && restriction.stopAfterReachingDate
                        )
                    ))
                    untilDateDialogVisible = false
                },
                onDismiss = { untilDateDialogVisible = false },
                oldValue = if (state.editRestrictionUnsaved is EditRestriction.UntilDate) {
                    state.editRestrictionUnsaved.date
                } else { null }
            )
        }
    }
}

@Preview
@Composable
fun EditRestrictionScreenPreview() {
    EditRestrictionScreen(
        state = ProfileScreenState(
            editRestrictionUnsaved = EditRestriction.UntilDate(LocalDateTime.now(), false)
        ),
        onAction = {}
    )
}