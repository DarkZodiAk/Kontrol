package com.darkzodiak.kontrol.home.components

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.darkzodiak.kontrol.home.HomeScreenState
import com.darkzodiak.kontrol.permission.getAccessibilityIntent
import com.darkzodiak.kontrol.permission.getAlertWindowIntent
import com.darkzodiak.kontrol.permission.getUsageStatsIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionSheet(
    state: HomeScreenState.Permissions,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    accessibilityLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    alertWindowLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    usageStatsLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val context = LocalContext.current

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Text(
            text = "Предоставьте следующие разрешения:",
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (!state.hasAccessibilityPermission) {
            PermissionCard(
                title = "Сервис доступности",
                permissionDescription = "Позволяет отслеживать и закрывать используемые приложения",
                onButtonClick = { accessibilityLauncher.launch(getAccessibilityIntent()) },
                modifier = Modifier.padding(8.dp)
            )
        }
        if (!state.hasAlertWindowPermission) {
            PermissionCard(
                title = "Показ всплывающих окон",
                permissionDescription = "Окна ограничивают доступ к приложениям в зависимости от настройки профилей",
                onButtonClick = { alertWindowLauncher.launch(context.getAlertWindowIntent()) },
                modifier = Modifier.padding(8.dp)
            )
        }
        if (!state.hasUsageStatsPermissions) {
            PermissionCard(
                title = "Доступ к статистике",
                permissionDescription = "Приложение сможет формировать отчет по использованию приложений",
                onButtonClick = { usageStatsLauncher.launch(context.getUsageStatsIntent()) },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}