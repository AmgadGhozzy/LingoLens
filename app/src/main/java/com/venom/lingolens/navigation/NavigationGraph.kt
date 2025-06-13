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
import com.venom.stackcard.ui.screen.quiz.MainLevelScreen
import com.venom.stackcard.ui.screen.quiz.QuizScreen
import com.venom.textsnap.ui.screens.OcrScreen
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.navigation.Screen
import com.venom.ui.screen.OnboardingScreens
import com.venom.ui.screen.SentenceScreen
import com.venom.ui.viewmodel.TranslateViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    translateViewModel: TranslateViewModel,
    ocrViewModel: OcrViewModel,
    startCamera: () -> Unit,
    imageSelector: () -> Unit,
    fileSelector: () -> Unit,
) {
    AnimatedNavHost(
        navController = navController, startDestination = Screen.Translation.route
    ) {
        // Translation Screen with optional text parameter
        composable(
            route = "${Screen.Translation.route}?${Screen.Translation.ARG_TEXT}={${Screen.Translation.ARG_TEXT}}",
            arguments = listOf(
                navArgument(Screen.Translation.ARG_TEXT) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "lingolens://translation?text={${Screen.Translation.ARG_TEXT}}"
                }
            )
        ) { backStackEntry ->
            val text = backStackEntry.arguments?.getString(Screen.Translation.ARG_TEXT)
            TranslationScreen(
                viewModel = translateViewModel,
                onNavigateToOcr = { navController.navigate(Screen.Ocr.route) },
                onNavigateToSentence = { text ->
                    navController.navigate(Screen.Sentence.createRoute(text))
                },
                initialText = text?.takeUnless { it == "null" || it == "{text}" }
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
            CardScreen(initialLevel = level,
                onNavigateToSentence = { text ->
                    navController.navigate(Screen.Sentence.createRoute(text))
                }
            )
        }

        // OCR Screen
        composable(Screen.Ocr.route) {
            OcrScreen(
                viewModel = ocrViewModel,
                onNavigateToTranslation = { text ->
                    navController.navigate(Screen.Translation.createRoute(text))
                },
                onFileClick = fileSelector,
                onCameraClick = startCamera,
                onGalleryClick = imageSelector
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreens(onGetStarted = { navController.navigate(Screen.Translation.createRoute()) })
        }

        // Phrasebook Screen
        composable(Screen.Phrases.route) {
            PhrasebookScreen()
        }

        composable(
            route = Screen.Sentence.route,
            arguments = listOf(navArgument("word") {
                type = NavType.StringType
                nullable = true
            }
            )
        ) { backStackEntry ->
            val word = backStackEntry.arguments?.getString("word")
            SentenceScreen(
                word = word,
                onNavigateBack = { navController.popBackStack() })
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

            QuizScreen(
                level = level, onComplete = { passed, nextLevel ->
                    if (passed && nextLevel != null) {
                        navController.navigate(Screen.StackCard.createRoute(level)) {
                            popUpTo(Screen.Quiz.MainLevel.route)
                        }
                    } else {
                        navController.popBackStack()
                    }
                },
                onNavigateToLearn = { level ->
                    navController.navigate(Screen.StackCard.createRoute(level))
                }
            )
        }
    }
}