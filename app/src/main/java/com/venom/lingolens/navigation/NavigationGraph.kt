package com.venom.lingolens.navigation

import android.net.Uri
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
import com.venom.quiz.ui.screen.MainLevelScreen
import com.venom.quiz.ui.screen.QuizScreen
import com.venom.quote.ui.screen.QuoteScreen
import com.venom.stackcard.ui.screen.BookmarksScreen
import com.venom.stackcard.ui.screen.CardScreen
import com.venom.textsnap.ui.screens.OcrScreen
import com.venom.ui.navigation.Screen
import com.venom.ui.screen.BookmarkHistoryScreen
import com.venom.ui.screen.ContentType
import com.venom.ui.screen.OnboardingScreens
import com.venom.ui.screen.SentenceScreen
import com.venom.wordcraftai.presentation.ui.screens.WordCraftScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startCamera: ((Uri?) -> Unit) -> Unit,
    imageSelector: ((Uri?) -> Unit) -> Unit,
    fileSelector: ((Uri?) -> Unit) -> Unit,
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Translation.route
    ) {
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
                onNavigateToOcr = { navController.navigate(Screen.Ocr.route) },
                onNavigateToSentence = { text ->
                    navController.navigate(Screen.Sentence.createRoute(text))
                },
                initialText = text?.takeUnless { it == "null" || it == "{text}" }
            )
        }

        composable(
            route = Screen.StackCard.route,
            arguments = listOf(navArgument("level") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level")?.let { levelId ->
                WordLevels.values().find { it.id == levelId }
            }
            CardScreen(
                initialLevel = level,
                onNavigateToSentence = { text ->
                    navController.navigate(Screen.Sentence.createRoute(text))
                }
            )
        }

        composable(Screen.Ocr.route) {
            OcrScreen(
                onNavigateToTranslation = { text ->
                    navController.navigate(Screen.Translation.createRoute(text))
                },
                onFileClick = fileSelector,
                onCameraClick = startCamera,
                onGalleryClick = imageSelector
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreens(
                onGetStarted = {
                    navController.navigate(Screen.Translation.createRoute())
                }
            )
        }

        composable(Screen.Phrases.route) {
            PhrasebookScreen()
        }

        composable(
            route = Screen.Sentence.route,
            arguments = listOf(navArgument("word") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val word = backStackEntry.arguments?.getString("word")
            SentenceScreen(
                word = word,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Dialog.route) {
            DialogScreen()
        }

        composable(
            route = Screen.History.route,
            arguments = listOf(navArgument("contentType") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val contentTypeString =
                backStackEntry.arguments?.getString("contentType") ?: "TRANSLATION"
            val contentType = ContentType.valueOf(contentTypeString)
            if (contentType == ContentType.STACKCARD)
                BookmarksScreen(
                    onBackClick = { navController.popBackStack() }
                )
            else
                BookmarkHistoryScreen(
                    navController = navController,
                    contentType = contentType,
                    onBackClick = { navController.popBackStack() }
                )
        }

        composable(Screen.Quiz.MainLevel.route) {
            MainLevelScreen(
                onNavigateToTest = { level ->
                    navController.navigate(Screen.Quiz.createRoute(level))
                },
                onNavigateToLearn = { level ->
                    navController.navigate(Screen.StackCard.createRoute(level))
                }
            )
        }

        composable(
            route = Screen.Quiz.LevelTest.route,
            arguments = listOf(navArgument("level") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val level = WordLevels.values().find {
                it.id == backStackEntry.arguments?.getString("level")
            } ?: WordLevels.Beginner

            QuizScreen(
                level = level,
                onComplete = { passed, nextLevel ->
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

        composable(Screen.WordCraft.route) {
            WordCraftScreen(
                onImageSelect = imageSelector
            )
        }

        composable(Screen.Quote.route) {
            QuoteScreen()
        }
    }
}