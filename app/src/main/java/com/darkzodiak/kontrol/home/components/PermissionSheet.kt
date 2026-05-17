package com.darkzodiak.kontrol.home.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
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
import com.darkzodiak.kontrol.R
import com.darkzodiak.kontrol.home.HomeScreenState

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
                title = "Специальные возможности",
                permissionDescription = "Позволяют отслеживать и закрывать ограниченные приложения",
                onButtonClick = {
                    val intent = context.getAccessibilityIntent()
                    if (intent.action == Settings.ACTION_SETTINGS) {
                        Toast.makeText(context, R.string.toast_accessibility_not_found, Toast.LENGTH_LONG).show()
                    }
                    accessibilityLauncher.launch(intent)
                },
                modifier = Modifier.padding(8.dp)
            )
        }
        if (!state.hasAlertWindowPermission) {
            PermissionCard(
                title = "Показ всплывающих окон",
                permissionDescription = "Окна ограничивают доступ к приложениям в зависимости от настройки профилей",
                onButtonClick = {
                    val intent = context.getAlertWindowIntent()
                    if (intent.action == Settings.ACTION_SETTINGS) {
                        Toast.makeText(context, R.string.toast_overlay_not_found, Toast.LENGTH_LONG).show()
                    }
                    alertWindowLauncher.launch(intent)
                },
                modifier = Modifier.padding(8.dp)
            )
        }
        if (!state.hasUsageStatsPermission) {
            PermissionCard(
                title = "Доступ к статистике",
                permissionDescription = "Приложение сможет формировать отчет по использованию приложений",
                onButtonClick = {
                    val intent = context.getUsageStatsIntent()
                    if (intent.action == Settings.ACTION_SETTINGS) {
                        Toast.makeText(context, R.string.toast_usage_access_not_found, Toast.LENGTH_LONG).show()
                    }
                    usageStatsLauncher.launch(intent)
                },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

fun Context.getUsageStatsIntent(): Intent {
    val intent = Intent(
        Settings.ACTION_USAGE_ACCESS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    if (intent.resolveActivity(packageManager) == null) intent.data = null
    return if (intent.resolveActivity(packageManager) != null) intent
    else Intent(Settings.ACTION_SETTINGS)
}

fun Context.getAccessibilityIntent(): Intent {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    return if (intent.resolveActivity(packageManager) != null) intent
    else Intent(Settings.ACTION_SETTINGS)
}

fun Context.getAlertWindowIntent(): Intent {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.fromParts("package", packageName, null)
    )
    if (intent.resolveActivity(packageManager) == null) intent.data = null
    return if (intent.resolveActivity(packageManager) != null) intent
    else Intent(Settings.ACTION_SETTINGS)
}