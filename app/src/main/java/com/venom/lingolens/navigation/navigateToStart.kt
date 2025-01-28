package com.venom.lingolens.navigation

import androidx.navigation.NavHostController
import com.venom.ui.navigation.Screen

fun NavHostController.navigateToStart(screen: Screen) {
    if (currentDestination?.route != screen.route) {
        navigate(screen.route) {
            popUpTo(graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}