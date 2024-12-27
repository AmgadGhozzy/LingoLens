package com.venom.lingolens.navigation

import androidx.navigation.NavHostController

fun NavHostController.navigateToStart(screen: Screen) {
    if (currentDestination?.route != screen.route) {
        navigate(screen.route) {
            popUpTo(graph.startDestinationId) {
                saveState = false
            }
            launchSingleTop = true
        }
    }
}