package com.darkzodiak.kontrol.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darkzodiak.kontrol.presentation.home.HomeScreenRoot
import com.darkzodiak.kontrol.presentation.profile.ProfileScreenRoot

@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.HomeScreen,
        modifier = modifier
    ) {
        composable<Route.HomeScreen> {
            HomeScreenRoot(
                onOpenProfile = { navController.navigate(Route.ProfileScreen(it)) },
                onNewProfile = { navController.navigate(Route.ProfileScreen(null)) }
            )
        }
        composable<Route.ProfileScreen> {
            ProfileScreenRoot(
                onBack = { navController.navigateUp() }
            )
        }
    }
}