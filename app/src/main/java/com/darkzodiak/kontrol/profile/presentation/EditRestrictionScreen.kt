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
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.presentation.components.NoRestrictionDialog
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
    var noRestrictionDialogVisible by rememberSaveable { mutableStateOf(false) }
    var passwordDialogVisible by rememberSaveable { mutableStateOf(false) }
    var randomPasswordDialogVisible by rememberSaveable { mutableStateOf(false) }
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

            RestrictionRow(
                restriction = getUntilDateRestrictionOrDefault(state.editRestrictionUnsaved),
                active = state.editRestrictionUnsaved is EditRestriction.UntilDate,
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
        }

        if (noRestrictionDialogVisible) {
            NoRestrictionDialog(
                onConfirm = {
                    onAction(ProfileAction.Restriction.UpdateType(EditRestriction.NoRestriction))
                },
                onDismiss = { noRestrictionDialogVisible = false }
            )
        }

        if (passwordDialogVisible) {
            PasswordDialog(
                onSave = {
                    onAction(ProfileAction.Restriction.UpdateType(EditRestriction.Password(it)))
                    passwordDialogVisible = false
                },
                onDismiss = { passwordDialogVisible = false },
                oldValue = getPasswordRestrictionOrDefault(state.editRestrictionUnsaved).password
            )
        }

        if (randomPasswordDialogVisible) {
            RandomPasswordDialog(
                onSave = {
                    onAction(ProfileAction.Restriction.UpdateType(EditRestriction.RandomText(it)))
                    randomPasswordDialogVisible = false
                },
                onDismiss = { randomPasswordDialogVisible = false },
                oldValue = getRandPasswordRestrictionOrDefault(state.editRestrictionUnsaved).length.toString()
            )
        }

        if (untilDateDialogVisible) {
            DelayDialog(
                type = DelayDialogType.RESTRICT_UNTIL,
                onSetPause = {
                    val restriction = getUntilDateRestrictionOrDefault(state.editRestrictionUnsaved)
                    onAction(ProfileAction.Restriction.UpdateType(
                        restriction.copy(
                            date = it,
                            stopAfterReachingDate = restriction.stopAfterReachingDate
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

fun getPasswordRestrictionOrDefault(restriction: EditRestriction): EditRestriction.Password {
    return restriction as? EditRestriction.Password ?: EditRestriction.Password.DEFAULT
}

fun getRandPasswordRestrictionOrDefault(restriction: EditRestriction): EditRestriction.RandomText {
    return restriction as? EditRestriction.RandomText ?: EditRestriction.RandomText.DEFAULT
}

fun getUntilDateRestrictionOrDefault(restriction: EditRestriction): EditRestriction.UntilDate {
    return restriction as? EditRestriction.UntilDate ?: EditRestriction.UntilDate.DEFAULT
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