package com.darkzodiak.kontrol.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darkzodiak.kontrol.presentation.home.HomeScreenRoot
import com.darkzodiak.kontrol.presentation.permission.PermissionScreenRoot
import com.darkzodiak.kontrol.presentation.profile.ProfileScreenRoot

@Composable
fun NavRoot(
    navController: NavHostController,
    hasPermissions: Boolean,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = if(hasPermissions) Route.HomeScreen else Route.PermissionScreen,
        modifier = modifier
    ) {
        composable<Route.PermissionScreen> {
            PermissionScreenRoot(onGoToApp = { navController.navigate(Route.HomeScreen) })
        }
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