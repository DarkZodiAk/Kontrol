@file:OptIn(ExperimentalMaterial3Api::class)

package com.darkzodiak.kontrol.presentation.home

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
import com.darkzodiak.kontrol.getAccessibilityIntent
import com.darkzodiak.kontrol.getAlertWindowIntent
import com.darkzodiak.kontrol.getUsageStatsIntent
import com.darkzodiak.kontrol.domain.Permission
import com.darkzodiak.kontrol.domain.model.Profile
import com.darkzodiak.kontrol.presentation.components.PermissionCard
import com.darkzodiak.kontrol.presentation.components.ProfileCard

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
    val permissionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var permissionSheetIsVisible by rememberSaveable { mutableStateOf(false) }


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
                            onClick = { permissionSheetIsVisible = true },
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
                    isLocked = false,
                    onClick = { onAction(HomeAction.OpenProfile(profile.id!!)) },
                    onActivate = { onAction(HomeAction.SwitchProfileState(profile.copy(isEnabled = true))) },
                    onPause = { /*TODO*/ },
                    onStop = { onAction(HomeAction.SwitchProfileState(profile.copy(isEnabled = false))) },
                    onDelete = { onAction(HomeAction.DeleteProfile(profile)) }
                )
            }
        }

        if (permissionSheetIsVisible) {
            ModalBottomSheet(
                sheetState = permissionSheetState,
                onDismissRequest = { permissionSheetIsVisible = false }
            ) {

                Text(
                    text = "Предоставьте следующие разрешения:",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

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