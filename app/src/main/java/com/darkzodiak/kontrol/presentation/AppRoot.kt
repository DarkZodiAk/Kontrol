package com.darkzodiak.kontrol.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun AppRoot(
    //viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController
) {
    NavRoot(navController = navController)
}