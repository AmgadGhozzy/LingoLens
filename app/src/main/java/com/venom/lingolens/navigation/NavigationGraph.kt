package com.venom.lingolens.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
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

@Composable
fun NavigationGraph(
    navController: NavHostController,
    ocrViewModel: OcrViewModel,
    startCamera: () -> Unit,
    imageSelector: () -> Unit
) {
    AnimatedNavHost(
        navController = navController, startDestination = Screen.Translation.route
    ) {
        // Translation Screen with optional text parameter
        composable(route = Screen.Translation.route, arguments = listOf(navArgument("text") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        }), deepLinks = listOf(navDeepLink {
            uriPattern = "lingolens://translation?text={text}"
        })
        ) { backStackEntry ->
            val text = backStackEntry.arguments?.getString("text")
            TranslationScreen(
                onNavigateToOcr = { navController.navigate(Screen.Ocr.route) }, initialText = text
            )
        }

        // Stack Card Screen with optional level parameter
        composable(route = Screen.StackCard.route, arguments = listOf(navArgument("level") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level")?.let { levelId ->
                WordLevels.values().find { it.id == levelId }
            }
            CardScreen(initialLevel = level)
        }

        // OCR Screen
        composable(Screen.Ocr.route) {
            OcrScreen(
                viewModel = ocrViewModel,
                onNavigateToTranslation = { text ->
                    navController.navigate(Screen.Translation.createRoute(text))
                },
                onCameraClick = startCamera,
                onFileClick = imageSelector,
                onGalleryClick = imageSelector
            )
        }

        // Phrasebook Screen
        composable(Screen.Phrases.route) {
            PhrasebookScreen()
        }

        // Dialog Screen
        composable(Screen.Dialog.route) {
            DialogScreen()
        }

        // Quiz screens
        composable(Screen.Quiz.MainLevel.route) {
            MainLevelScreen(onNavigateToTest = { level ->
                navController.navigate(Screen.Quiz.createRoute(level))
            }, onNavigateToLearn = { level ->
                navController.navigate(Screen.StackCard.createRoute(level))
            })
        }

        // Quiz level test
        composable(route = Screen.Quiz.LevelTest.route, arguments = listOf(navArgument("level") {
            type = NavType.StringType
        })) { backStackEntry ->
            val level = WordLevels.values().find {
                it.id == backStackEntry.arguments?.getString("level")
            } ?: WordLevels.Beginner

            QuizScreen(level = level, onComplete = { passed, nextLevel ->
                if (passed && nextLevel != null) {
                    navController.navigate(Screen.StackCard.createRoute(level)) {
                        popUpTo(Screen.Quiz.MainLevel.route)
                    }
                } else {
                    navController.popBackStack()
                }
            })
        }
    }
}