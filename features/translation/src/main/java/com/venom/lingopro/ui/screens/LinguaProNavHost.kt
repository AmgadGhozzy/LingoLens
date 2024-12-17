package com.venom.lingopro.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.venom.lingopro.ui.viewmodel.ViewType

@Composable
fun LingoProNavHost(
    navController: NavHostController, modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Translation.route,
        modifier = modifier
    ) {
        composable(Screen.Translation.route) {
            TranslationScreen(onDismiss = { navController.popBackStack() },
                // In TranslationScreen or wherever you navigate from
                onNavigateToBookmarks = {
                    navController.navigate("bookmarks_history/BOOKMARKS")
                },
                onNavigateToHistory = {
                    navController.navigate("bookmarks_history/HISTORY")
                })
        }

        composable("bookmarks_history/{viewType}") { backStackEntry ->
            val viewType = backStackEntry.arguments?.getString("viewType")?.let {
                ViewType.valueOf(it)
            } ?: ViewType.BOOKMARKS

            BookmarkHistoryScreen(
                onBackClick = { navController.popBackStack() },
                viewType = viewType,
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Translation : Screen("translation")
    object History : Screen("history")
    object Bookmarks : Screen("bookmarks")
    object Dictionary : Screen("dictionary")
}