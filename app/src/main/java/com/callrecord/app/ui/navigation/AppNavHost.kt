package com.callrecord.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.callrecord.app.ui.screens.DetailScreen
import com.callrecord.app.ui.screens.HomeScreen
import com.callrecord.app.ui.screens.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        composable(NavRoutes.Home.route) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onOpenSettings = { navController.navigate(NavRoutes.Settings.route) },
                onOpenDetail = { recordingId ->
                    navController.navigate(NavRoutes.Detail.createRoute(recordingId))
                }
            )
        }
        composable(NavRoutes.Settings.route) {
            SettingsScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = NavRoutes.Detail.route,
            arguments = listOf(navArgument("recordingId") { type = NavType.LongType })
        ) {
            DetailScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
