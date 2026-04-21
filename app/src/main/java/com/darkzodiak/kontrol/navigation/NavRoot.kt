package com.darkzodiak.kontrol.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darkzodiak.kontrol.core.presentation.MainScreen
import com.darkzodiak.kontrol.core.presentation.NavItem
import com.darkzodiak.kontrol.home.HomeScreenRoot
import com.darkzodiak.kontrol.profile.presentation.ProfileScreenRoot
import com.darkzodiak.kontrol.profile.presentation.appList.AppListScreenRoot
import com.darkzodiak.kontrol.profile.presentation.appRestriction.AppRestrictionScreenRoot
import com.darkzodiak.kontrol.profile.presentation.editRestriction.EditRestrictionScreenRoot
import com.darkzodiak.kontrol.statistics.presentation.StatisticsScreenRoot

@Composable
fun NavRoot(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.MainScreen,
        modifier = modifier,
        enterTransition = {
            fadeIn(tween(250)) + scaleIn(tween(250), 0.95f)
        },
        exitTransition = { fadeOut(tween(250)) },
        popEnterTransition = { fadeIn(tween(250)) },
        popExitTransition = {
            fadeOut(tween(250)) + scaleOut(tween(250), 0.95f)
        }
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
                    NavItem.STATISTICS -> {
                        StatisticsScreenRoot()
                    }
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