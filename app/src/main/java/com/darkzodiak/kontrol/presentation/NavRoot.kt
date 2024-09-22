package com.darkzodiak.kontrol.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.MainScreen,
        modifier = modifier
    ) {
        composable<Route.MainScreen> {

        }
        composable<Route.ProfileScreen> {

        }
    }
}