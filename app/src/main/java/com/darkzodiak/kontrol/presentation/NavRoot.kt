package com.darkzodiak.kontrol.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.darkzodiak.kontrol.presentation.home.HomeScreenRoot
import com.darkzodiak.kontrol.presentation.profile.AppListScreenRoot
import com.darkzodiak.kontrol.presentation.profile.EditRestrictionScreenRoot
import com.darkzodiak.kontrol.presentation.profile.ProfileScreenRoot
import com.darkzodiak.kontrol.presentation.profile.ProfileViewModel

@Composable
fun NavRoot(
    navController: NavHostController,
    modifier: Modifier = Modifier
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

        navigation<Route.Profile>(startDestination = Route.ProfileScreen(null)) {
            composable<Route.ProfileScreen> {
                val parentEntry = remember { navController.getBackStackEntry(Route.Profile) }
                ProfileScreenRoot(
                    viewModel = hiltViewModel<ProfileViewModel>(parentEntry),
                    toAppList = { navController.navigate(Route.AppListScreen) },
                    toRestrictions = { navController.navigate(Route.EditRestrictionScreen) },
                    onBack = { navController.navigateUp() }
                )
            }
            composable<Route.AppListScreen> {
                val parentEntry = remember { navController.getBackStackEntry(Route.Profile) }
                AppListScreenRoot(
                    viewModel = hiltViewModel<ProfileViewModel>(parentEntry),
                    onBack = { navController.navigateUp() }
                )
            }
            composable<Route.EditRestrictionScreen> {
                val parentEntry = remember { navController.getBackStackEntry(Route.Profile) }
                EditRestrictionScreenRoot(
                    viewModel = hiltViewModel<ProfileViewModel>(parentEntry),
                    onBack = { navController.navigateUp() }
                )
            }
        }
    }
}