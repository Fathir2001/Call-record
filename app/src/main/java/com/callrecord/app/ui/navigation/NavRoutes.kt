package com.callrecord.app.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Settings : NavRoutes("settings")
    data object Detail : NavRoutes("detail/{recordingId}") {
        fun createRoute(recordingId: Long): String = "detail/$recordingId"
    }
}
