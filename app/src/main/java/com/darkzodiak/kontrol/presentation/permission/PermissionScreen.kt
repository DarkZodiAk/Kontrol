package com.darkzodiak.kontrol.presentation.permission

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darkzodiak.kontrol.data.KontrolService
import com.darkzodiak.kontrol.getUsageStatsIntent
import com.darkzodiak.kontrol.hasUsageStatisticsPermission

@Composable
fun PermissionScreenRoot(
    viewModel: PermissionViewModel = hiltViewModel(),
    onGoToApp: () -> Unit
) {
    val context = LocalContext.current

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

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(20))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = state.hasUsageStatsPermission, enabled = false, onCheckedChange = { })
                    Text(text = "Usage stats")
                }
                Button(
                    onClick = { usageStatsLauncher.launch(context.getUsageStatsIntent()) },
                    enabled = !state.hasUsageStatsPermission
                ) {
                    Text(text = "Настройки")
                }
            }

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