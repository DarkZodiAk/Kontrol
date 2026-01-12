package com.darkzodiak.kontrol.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darkzodiak.kontrol.core.presentation.MainScreen
import com.darkzodiak.kontrol.core.presentation.NavItem
import com.darkzodiak.kontrol.home.HomeScreenRoot
import com.darkzodiak.kontrol.profile.presentation.appList.AppListScreenRoot
import com.darkzodiak.kontrol.profile.presentation.editRestriction.EditRestrictionScreenRoot
import com.darkzodiak.kontrol.profile.presentation.ProfileScreenRoot
import com.darkzodiak.kontrol.profile.presentation.appRestriction.AppRestrictionScreenRoot

@Composable
fun NavRoot(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.MainScreen,
        modifier = modifier
    ) {
        composable<Route.MainScreen> {
            MainScreen { item ->
                when (item) {
                    NavItem.PROFILES -> {
                        HomeScreenRoot(
                            onOpenProfile = { id, inProtectedMode ->
                                navController.navigate(Route.ProfileScreen(id, inProtectedMode))
                            },
                            onNewProfile = { navController.navigate(Route.ProfileScreen(null)) }
                        )
                    }
                    NavItem.STATISTICS -> {}
                }
            }
        }

        composable<Route.ProfileScreen> {
            ProfileScreenRoot(
                toAppList = { navController.navigate(Route.AppListScreen) },
                toAppRestrictions = { navController.navigate(Route.AppRestrictionScreen) },
                toEditRestrictions = { navController.navigate(Route.EditRestrictionScreen) },
                onBack = { navController.navigateUp() }
            )
        }

        composable<Route.AppListScreen> {
            AppListScreenRoot(onBack = { navController.navigateUp() })
        }

        composable<Route.AppRestrictionScreen> {
            AppRestrictionScreenRoot(onBack = { navController.navigateUp() })
        }

        composable<Route.EditRestrictionScreen> {
            EditRestrictionScreenRoot(onBack = { navController.navigateUp() })
        }
    }
}