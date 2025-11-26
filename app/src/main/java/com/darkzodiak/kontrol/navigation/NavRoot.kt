package com.darkzodiak.kontrol.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darkzodiak.kontrol.home.HomeScreenRoot
import com.darkzodiak.kontrol.profile.presentation.appList.AppListScreenRoot
import com.darkzodiak.kontrol.profile.presentation.editRestriction.EditRestrictionScreenRoot
import com.darkzodiak.kontrol.profile.presentation.ProfileScreenRoot

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

        composable<Route.ProfileScreen> {
            ProfileScreenRoot(
                toAppList = { navController.navigate(Route.AppListScreen) },
                toEditRestrictions = { navController.navigate(Route.EditRestrictionScreen) },
                onBack = { navController.navigateUp() }
            )
        }

        composable<Route.AppListScreen> {
            AppListScreenRoot(onBack = { navController.navigateUp() })
        }

        composable<Route.EditRestrictionScreen> {
            EditRestrictionScreenRoot(onBack = { navController.navigateUp() })
        }
    }
}