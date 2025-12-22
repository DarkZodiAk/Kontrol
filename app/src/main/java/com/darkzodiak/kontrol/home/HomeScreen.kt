@file:OptIn(ExperimentalMaterial3Api::class)

package com.darkzodiak.kontrol.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darkzodiak.kontrol.core.presentation.delayDialog.DelayDialog
import com.darkzodiak.kontrol.core.presentation.delayDialog.DelayDialogType
import com.darkzodiak.kontrol.permission.domain.Permission
import com.darkzodiak.kontrol.profile.domain.Profile
import com.darkzodiak.kontrol.home.profileCard.ProfileCard
import com.darkzodiak.kontrol.home.components.PasswordDialog
import com.darkzodiak.kontrol.home.components.PermissionSheet
import com.darkzodiak.kontrol.home.components.RandomTextDialog
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.ProfileState

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenProfile: (Long) -> Unit,
    onNewProfile: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.OpenProfile -> onOpenProfile(event.id)
                is HomeEvent.ShowError -> {
                    Toast.makeText(context, event.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    HomeScreen(
        state = viewModel.state,
        onAction = { action ->
            if (action is HomeAction.NewProfile) onNewProfile()
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onAction: (HomeAction) -> Unit
) {
    val permissionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var permissionSheetVisible by rememberSaveable { mutableStateOf(false) }

    val accessibilityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onAction(HomeAction.UpdatePermissionInfo(Permission.ACCESSIBILITY))
    }
    val alertWindowLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onAction(HomeAction.UpdatePermissionInfo(Permission.SYSTEM_ALERT_WINDOW))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(HomeAction.NewProfile) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create new profile")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (state.permissions.hasEssentialPermissions.not()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(start = 12.dp, top = 16.dp, bottom = 4.dp, end = 8.dp)
                    ) {
                        Text(
                            text = "Требуются разрешения для работы приложения",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { permissionSheetVisible = true },
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                                disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor,
                                disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor
                            )
                        ) {
                            Text(text = "Предоставить")
                        }
                    }
                }
            }

            items(state.profiles) { profile ->
                ProfileCard(
                    title = profile.name,
                    state = profile.state,
                    now = state.curTime,
                    onIntent = { intent ->
                        onAction(HomeAction.RequestProfileAction(profile, intent))
                    }
                )
            }
        }

        if (permissionSheetVisible) {
            PermissionSheet(
                state = state.permissions,
                sheetState = permissionSheetState,
                onDismiss = { permissionSheetVisible = false },
                accessibilityLauncher = accessibilityLauncher,
                alertWindowLauncher = alertWindowLauncher
            )
        }

        if (state.delayDialogVisible) {
            DelayDialog(
                type = DelayDialogType.PAUSE,
                onSetPause = { onAction(HomeAction.Delay.Save(it)) },
                onDismiss = { onAction(HomeAction.Delay.Dismiss) },
                oldValue = state.oldPauseDate
            )
        }

        if (state.restrictionDialogVisible) { when(state.curRestriction) {
            is EditRestriction.Password -> {
                PasswordDialog(
                    password = state.curRestriction.password,
                    onDismiss = { onAction(HomeAction.RestrictionNotPassed) },
                    onSuccess = { onAction(HomeAction.RestrictionPassed) }
                )
            }
            is EditRestriction.RandomText -> {
                RandomTextDialog(
                    textLength = state.curRestriction.length,
                    onDismiss = { onAction(HomeAction.RestrictionNotPassed) },
                    onSuccess = { onAction(HomeAction.RestrictionPassed) }
                )
            }
            else -> Unit
        } }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = HomeScreenState(
            listOf(Profile(name = "hello?"), Profile(name = "Other profile", state = ProfileState.Active))
        ),
        onAction = {}
    )
}