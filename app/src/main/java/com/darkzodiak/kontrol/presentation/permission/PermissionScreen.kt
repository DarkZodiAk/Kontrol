package com.darkzodiak.kontrol.presentation.permission

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darkzodiak.kontrol.data.KontrolService
import com.darkzodiak.kontrol.getAccessibilityIntent
import com.darkzodiak.kontrol.getAlertWindowIntent
import com.darkzodiak.kontrol.getUsageStatsIntent
import com.darkzodiak.kontrol.hasAccessibilityPermission
import com.darkzodiak.kontrol.hasAlertWindowPermission
import com.darkzodiak.kontrol.hasUsageStatisticsPermission

@Composable
fun PermissionScreenRoot(
    viewModel: PermissionViewModel = hiltViewModel(),
    onGoToApp: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.onAction(PermissionAction.SendPermissionInfo(Permission.USAGE_STATS_ACCESS, context.hasUsageStatisticsPermission()))
        viewModel.onAction(PermissionAction.SendPermissionInfo(Permission.ACCESSIBILITY, context.hasAccessibilityPermission()))
        viewModel.onAction(PermissionAction.SendPermissionInfo(Permission.SYSTEM_ALERT_WINDOW, context.hasAlertWindowPermission()))
    }

    PermissionScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                PermissionAction.GoToApp -> {
                    context.startService(KontrolService.buildActionIntent(context, KontrolService.ACTION_START))
                    onGoToApp()
                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun PermissionScreen(
    state: PermissionState,
    onAction: (PermissionAction) -> Unit
) {
    val context = LocalContext.current
    val usageStatsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onAction(PermissionAction.SendPermissionInfo(Permission.USAGE_STATS_ACCESS, context.hasUsageStatisticsPermission()))
    }
    val accessibilityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onAction(PermissionAction.SendPermissionInfo(Permission.ACCESSIBILITY, context.hasAccessibilityPermission()))
    }
    val alertWindowLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onAction(PermissionAction.SendPermissionInfo(Permission.SYSTEM_ALERT_WINDOW, context.hasAlertWindowPermission()))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(text = "Предоставьте следующие разрешения")
            Spacer(modifier = Modifier.height(32.dp))

            PermissionCard(
                title = "Usage stats",
                hasPermission = state.hasUsageStatsPermission,
                onButtonClick = { usageStatsLauncher.launch(context.getUsageStatsIntent()) }
            )
            Spacer(modifier = Modifier.height(4.dp))
            PermissionCard(
                title = "Accessibility",
                hasPermission = state.hasAccessibilityPermission,
                onButtonClick = { accessibilityLauncher.launch(getAccessibilityIntent()) }
            )
            Spacer(modifier = Modifier.height(4.dp))
            PermissionCard(
                title = "Overlay",
                hasPermission = state.hasAlertWindowPermission,
                onButtonClick = { alertWindowLauncher.launch(context.getAlertWindowIntent()) }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onAction(PermissionAction.GoToApp) },
                enabled = state.hasAllPermissions
            ) {
                Text(text = "Продолжить")
            }
        }
    }
}

@Preview
@Composable
private fun PermissionScreenPreview() {
    PermissionScreen(
        state = PermissionState(),
        onAction = { }
    )
}