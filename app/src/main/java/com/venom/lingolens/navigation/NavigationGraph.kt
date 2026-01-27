package com.venom.lingolens.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.venom.dialog.ui.screen.DialogScreen
import com.venom.domain.model.QuizMode
import com.venom.domain.model.WordLevels
import com.venom.lingopro.ui.screens.TranslationScreen
import com.venom.phrase.ui.screen.PhrasebookScreen
import com.venom.quiz.ui.screen.CategoriesScreen
import com.venom.quiz.ui.screen.LevelScreen
import com.venom.quiz.ui.screen.QuizMainScreen
import com.venom.quiz.ui.screen.QuizScreen
import com.venom.quiz.ui.screen.TestsScreen
import com.venom.quote.ui.screen.QuoteScreen
import com.venom.stackcard.ui.screen.WordMasteryScreen
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
            route = Screen.StackCard.route
        ) {
            WordMasteryScreen(
                isGenerative = false,
                onBack = { navController.popBackStack() }
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
            BookmarkHistoryScreen(
                navController = navController,
                contentType = contentType,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Main Quiz Hub - Shows both vocab levels and classes
        composable(Screen.Quiz.route) {
            QuizMainScreen(
                onNavigateToLevels = {
                    navController.navigate(Screen.Quiz.Levels.route)
                },
                onNavigateToTest = { level ->
                    navController.navigate(Screen.Quiz.LevelTest.createRoute(level))
                },
                onNavigateToLearn = { level ->
                    navController.navigate(Screen.StackCard.createRoute(level))
                },
                onNavigateToClass = { classId ->
                    navController.navigate(Screen.QuizCategories.createRoute(classId))
                }
            )
        }

        // Detailed vocab levels screen with expandable cards
        composable(Screen.Quiz.Levels.route) {
            LevelScreen(
                onNavigateToTest = { level ->
                    navController.navigate(Screen.Quiz.LevelTest.createRoute(level))
                },
                onNavigateToLearn = { level ->
                    navController.navigate(Screen.StackCard.createRoute(level))
                }
            )
        }

        // Categories for a specific class
        composable(
            route = Screen.QuizCategories.route,
            arguments = listOf(navArgument("classId") { type = NavType.IntType })
        ) {
            CategoriesScreen(
                onBackClick = { navController.navigateUp() },
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.QuizTests.createRoute(categoryId))
                }
            )
        }

        // Tests within a category
        composable(
            route = Screen.QuizTests.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) {
            TestsScreen(
                onBackClick = { navController.navigateUp() },
                onTestClick = { testId ->
                    navController.navigate(Screen.QuizExam.createRoute(testId))
                }
            )
        }

        // Actual exam/test taking screen
        composable(
            route = Screen.QuizExam.route,
            arguments = listOf(navArgument("testId") { type = NavType.IntType })
        ) {
            QuizScreen(
                mode = QuizMode.Test(
                    testId = it.arguments?.getInt("testId") ?: 0
                ),
                onBack = { navController.navigateUp() },
                onComplete = { _, _ ->
                    navController.popBackStack(
                        Screen.Quiz.route,
                        false
                    )
                }
            )
        }

        // Vocabulary level test
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
                mode = QuizMode.Word(level),
                onBack = { navController.popBackStack() },
                onComplete = { passed, _ ->
                    if (passed) {
                        navController.navigate(Screen.StackCard.createRoute(level)) {
                            popUpTo(Screen.Quiz.route)
                        }
                    } else {
                        navController.popBackStack()
                    }
                },
                onLearnClick = { navController.navigate(Screen.StackCard.createRoute(it)) }
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