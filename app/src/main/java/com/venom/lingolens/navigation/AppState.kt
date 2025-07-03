package com.venom.lingolens.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.venom.ui.navigation.Screen

class AppState(
    val navController: NavHostController
) {
    var currentBackStackEntry: NavBackStackEntry? by mutableStateOf(null)
    var showSettings by mutableStateOf(false)
    var showAbout by mutableStateOf(false)

    val currentRoute: String?
        get() = currentBackStackEntry?.destination?.route

    val currentScreen: Screen
        get() = Screen.fromRoute(currentRoute ?: Screen.Translation.route)

    val shouldShowTopBar: Boolean
        get() = currentRoute != Screen.Quiz.LevelTest.route &&
                currentRoute != Screen.Onboarding.route &&
                currentRoute != Screen.Sentence.route &&
                currentRoute != Screen.WordCraft.route &&
                currentRoute?.startsWith("history") != true

    val shouldShowBottomBar: Boolean
        get() = currentRoute != Screen.Ocr.route &&
                currentRoute != Screen.Quiz.LevelTest.route &&
                currentRoute != Screen.Onboarding.route &&
                currentRoute != Screen.Sentence.route &&
                currentRoute?.startsWith("history") != true

    fun navigateToScreen(screen: Screen) {
        when (screen) {
            is Screen.Translation -> navController.navigate(screen.route) {
                popUpTo(Screen.Translation.route) { inclusive = true }
            }
            else -> navController.navigate(screen.route) {
                popUpTo(Screen.Translation.route)
            }
        }
    }

    fun navigateToHistory(contentType: String) {
        navController.navigate(Screen.History.createRoute(contentType))
    }

    fun handleBackPress() {
        when {
            showSettings -> showSettings = false
            showAbout -> showAbout = false
            else -> navController.popBackStack()
        }
    }
}