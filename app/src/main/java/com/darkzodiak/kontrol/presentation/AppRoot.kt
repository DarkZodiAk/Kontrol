package com.darkzodiak.kontrol.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkzodiak.kontrol.MainEvent
import com.darkzodiak.kontrol.MainViewModel
import com.darkzodiak.kontrol.data.KontrolService

@Composable
fun AppRoot(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.serviceEvent.collect { event ->
            when(event) {
                MainEvent.StartKontrolService -> {
                    context.startService(KontrolService.buildActionIntent(context, KontrolService.ACTION_START))
                }
                else -> Unit
            }
        }
    }

    NavRoot(navController = navController)
}