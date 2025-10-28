@file:OptIn(ExperimentalMaterial3Api::class)

package com.darkzodiak.kontrol.home

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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.darkzodiak.kontrol.permission.getAccessibilityIntent
import com.darkzodiak.kontrol.permission.getAlertWindowIntent
import com.darkzodiak.kontrol.permission.getUsageStatsIntent
import com.darkzodiak.kontrol.permission.domain.Permission
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.Profile
import com.darkzodiak.kontrol.core.presentation.PermissionCard
import com.darkzodiak.kontrol.core.presentation.ProfileCard
import com.darkzodiak.kontrol.home.components.EnterPasswordDialog

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenProfile: (Long) -> Unit,
    onNewProfile: () -> Unit
) {
    HomeScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                HomeAction.NewProfile -> onNewProfile()
                is HomeAction.OpenProfile -> onOpenProfile(action.id)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    var passwordDialogVisible by rememberSaveable { mutableStateOf(false) }

    val permissionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var permissionSheetVisible by rememberSaveable { mutableStateOf(false) }


    val context = LocalContext.current
    val usageStatsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onAction(HomeAction.UpdatePermissionInfo(Permission.USAGE_STATS_ACCESS))
    }
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

    // Show dialog depending on profile's restriction
    fun showUnlockDialog(editRestriction: EditRestriction) {
        when(editRestriction) {
            EditRestriction.NoRestriction -> { }
            is EditRestriction.Password -> { passwordDialogVisible = true }
            is EditRestriction.RandomText -> { passwordDialogVisible = true }
        }
    }

    // Executes given action on profile if it isn't enabled or it has no Edit Restriction
    // Otherwise it will save action and show appropriate dialog to unlock action
    fun tryExecuteProfileAction(profile: Profile, action: HomeAction) {
        if(profile.isEnabled && profile.editRestriction !is EditRestriction.NoRestriction) {
            onAction(HomeAction.PrepareForUnlock(action, profile.editRestriction))
            showUnlockDialog(profile.editRestriction)
        } else onAction(action)
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
            if (!state.hasAllPermissions) {
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
                    infoText = if(profile.isEnabled) "Активен" else "Неактивен",
                    title = profile.name,
                    isActive = profile.isEnabled,
                    onClick = {
                        tryExecuteProfileAction(
                            profile = profile,
                            action = HomeAction.OpenProfile(profile.id!!)
                        )
                    },
                    onActivate = {
                        onAction(HomeAction.ChangeProfileState(profile, ProfileStateAction.Activate))
                    },
                    onPause = { /*TODO*/ },
                    onStop = {
                        tryExecuteProfileAction(
                            profile = profile,
                            action = HomeAction.ChangeProfileState(profile, ProfileStateAction.Stop)
                        )
                    },
                    onDelete = {
                        tryExecuteProfileAction(
                            profile = profile,
                            action = HomeAction.DeleteProfile(profile)
                        )
                    }
                )
            }
        }

        if(permissionSheetVisible) {
            ModalBottomSheet(
                sheetState = permissionSheetState,
                onDismissRequest = { permissionSheetVisible = false }
            ) {
                Text(
                    text = "Предоставьте следующие разрешения:",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // TODO(): UsageStatsPermission is not necessary for app blocking. Move to app usage module (when it'll be created)
                if (!state.hasUsageStatsPermission) {
                    PermissionCard(
                        title = "Usage stats",
                        onButtonClick = { usageStatsLauncher.launch(context.getUsageStatsIntent()) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (!state.hasAccessibilityPermission) {
                    PermissionCard(
                        title = "Accessibility",
                        onButtonClick = { accessibilityLauncher.launch(getAccessibilityIntent()) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (!state.hasAlertWindowPermission) {
                    PermissionCard(
                        title = "Overlay",
                        onButtonClick = { alertWindowLauncher.launch(context.getAlertWindowIntent()) }
                    )
                }
            }
        }

        // All unlock dialogs should call onAction(state.pendingAction) on success
        if(passwordDialogVisible) {
            EnterPasswordDialog(
                passRestriction = state.curRestriction,
                onDismiss = { passwordDialogVisible = false },
                onSuccess = {
                    onAction(state.pendingAction)
                    passwordDialogVisible = false
                }
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = HomeState(
            listOf(Profile(name = "hello?"), Profile(name = "Other profile", isEnabled = true))
        ),
        onAction = {}
    )
}