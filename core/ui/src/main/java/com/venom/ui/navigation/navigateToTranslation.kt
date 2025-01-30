package com.venom.ui.navigation

import androidx.navigation.NavController

fun NavController.navigateToTranslation(text: String?) {
    val route = Screen.Translation.createRoute(text)

    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
            inclusive = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
