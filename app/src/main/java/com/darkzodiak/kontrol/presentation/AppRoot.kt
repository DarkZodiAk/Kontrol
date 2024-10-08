package com.darkzodiak.kontrol.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darkzodiak.kontrol.MainViewModel
import com.darkzodiak.kontrol.data.KontrolService

@Composable
fun AppRoot(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.uiEvent.collect { canStartService ->
            if(canStartService) {
                context.startService(KontrolService.buildActionIntent(context, KontrolService.ACTION_START))
            }
        }
    }

    NavRoot(
        navController = navController,
        hasPermissions = viewModel.hasPermissions
    )
}