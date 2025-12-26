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
        get() = when {
            // Quiz-related screens - hide top bar (they have custom headers)
            currentRoute == Screen.Quiz.route -> false
            currentRoute == Screen.Quiz.Levels.route -> false
            currentRoute?.startsWith("quiz/test") == true -> false
            currentRoute?.startsWith("quiz/exam") == true -> false
            currentRoute?.startsWith("quiz/categories") == true -> false
            currentRoute?.startsWith("quiz/tests") == true -> false

            // Other screens - hide top bar
            currentRoute == Screen.Onboarding.route -> false
            currentRoute == Screen.Sentence.route -> false
            currentRoute == Screen.Phrases.route -> false
            currentRoute == Screen.WordCraft.route -> false
            currentRoute == Screen.Quote.route -> false
            currentRoute?.startsWith("history") == true -> false

            // Show top bar for all other screens
            else -> true
        }

    val shouldShowBottomBar: Boolean
        get() = when {
            // Hide bottom bar for these screens
            currentRoute == Screen.Ocr.route -> false
            currentRoute?.startsWith("quiz/test") == true -> false
            currentRoute?.startsWith("quiz/exam") == true -> false
            currentRoute?.startsWith("quiz/categories") == true -> false
            currentRoute?.startsWith("quiz/tests") == true -> false
            currentRoute == Screen.Onboarding.route -> false
            currentRoute == Screen.Sentence.route -> false
            currentRoute?.startsWith("history") == true -> false

            // Show bottom bar for all other screens (including quiz hub and levels)
            else -> true
        }

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