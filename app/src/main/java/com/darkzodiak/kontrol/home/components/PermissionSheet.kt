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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionSheet(
    state: HomeScreenState.Permissions,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    accessibilityLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    alertWindowLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
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