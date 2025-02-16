package com.venom.lingolens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.venom.dialog.ui.screen.DialogScreen
import com.venom.domain.model.WordLevels
import com.venom.lingopro.ui.screens.TranslationScreen
import com.venom.phrase.ui.screen.PhrasebookScreen
import com.venom.stackcard.ui.screen.CardScreen
import com.venom.stackcard.ui.screen.MainLevelScreen
import com.venom.stackcard.ui.screen.QuizScreen
import com.venom.textsnap.ui.screens.OcrScreen
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.navigation.Screen
import com.venom.ui.navigation.Screen.StackCard

@Composable
fun NavigationGraph(
    ocrViewModel: OcrViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startCamera: () -> Unit,
    imageSelector: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Translation.route,
        modifier = modifier
    ) {
        composable(
            Screen.Translation.route,
            arguments = listOf(navArgument("text") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "lingolens://translation/{text}" })
        ) { backStackEntry ->
            val text = backStackEntry.arguments?.getString("text")
            TranslationScreen(
                onNavigateToOcr = { navController.navigate(Screen.Ocr.route) },
                initialText = text
            )
        }

        composable(Screen.Ocr.route) {
            OcrScreen(
                viewModel = ocrViewModel,
                onNavigateToTranslation = { text ->
                    navController.navigate(Screen.Translation.createRoute(text))
                },
                onCameraClick = startCamera,
                onFileClick = { imageSelector() },
                onGalleryClick = { imageSelector() }
            )
        }

        composable(Screen.Phrases.route) {
            PhrasebookScreen()
        }

        composable(Screen.Dialog.route) {
            DialogScreen()
        }

        composable(StackCard.route) {
            CardScreen()
        }

        // Quiz routes
        composable(Screen.Quiz.MainLevel.route) {
            MainLevelScreen(
                onNavigateToTest = { level ->
                    navController.navigate(Screen.Quiz.createTestRoute(level))
                },
                onNavigateToLearn = { level ->
                    navController.navigate(StackCard.createRoute(level))
                }
            )
        }

        composable(
            route = Screen.Quiz.LevelTest.route,
            arguments = listOf(navArgument("level") { type = NavType.StringType })
        ) { backStackEntry ->
            val level = WordLevels.values().find {
                it.id == backStackEntry.arguments?.getString("level")
            } ?: WordLevels.Beginner

            QuizScreen(
                level = level,
                onComplete = { passed, nextLevel ->
                    if (passed && nextLevel != null) {
                        navController.navigate(StackCard.createRoute(level)) {
                            popUpTo(Screen.Quiz.MainLevel.route)
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = StackCard.route,
            arguments = listOf(navArgument("level") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level")?.let { levelId ->
                WordLevels.values().find { it.id == levelId }
            }

            CardScreen(initialLevel = level)
        }
    }
}