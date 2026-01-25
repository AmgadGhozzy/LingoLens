package com.venom.ui.navigation

import com.venom.domain.model.WordLevels

sealed class Screen(val route: String) {
    object Translation : Screen("translation") {
        const val ARG_TEXT = "text"
        fun createRoute(text: String? = null) =
            if (text.isNullOrBlank()) route
            else "$route?$ARG_TEXT=$text"
    }

    object Ocr : Screen("ocr")
    object Dialog : Screen("dialog")
    object StackCard : Screen("stackcard/{level}") {
        fun createRoute(level: WordLevels? = null) = "stackcard/${level?.id ?: ""}"
        fun createRoute(levelId: String) = "stackcard/$levelId"
    }

    object Phrases : Screen("phrases")
    object Sentence : Screen("sentence/{word}") {
        fun createRoute(word: String) = "sentence/$word"
    }
    object Dictionary : Screen("dictionary")
    object History : Screen("history/{contentType}") {
        fun createRoute(contentType: String) = "history/$contentType"
    }
    object Bookmarks : Screen("bookmarks")
    object Onboarding : Screen("onboarding")

    object WordCraft : Screen("wordcraft")


    object Quote : Screen("quote")

    // Main quiz screen - hub showing vocab levels and classes
    object Quiz : Screen("quiz") {
        // Detailed vocab levels screen
        object Levels : Screen("quiz/levels")

        // Vocabulary level test
        object LevelTest : Screen("quiz/test/{level}") {
            fun createRoute(level: WordLevels) = "quiz/test/${level.id}"
        }
    }

    // Grammar / exam / classes flow
    object QuizCategories : Screen("quiz/categories/{classId}") {
        fun createRoute(classId: Int) = "quiz/categories/$classId"
    }

    object QuizTests : Screen("quiz/tests/{categoryId}") {
        fun createRoute(categoryId: Int) = "quiz/tests/$categoryId"
    }

    object QuizExam : Screen("quiz/exam/{testId}") {
        fun createRoute(testId: Int) = "quiz/exam/$testId"
    }

    companion object {
        fun fromRoute(route: String): Screen {
            return when {
                route.startsWith("translation") -> Translation
                route == "ocr" -> Ocr
                route == "dialog" -> Dialog
                route.startsWith("stackcard") -> StackCard
                route == "quiz" -> Quiz
                route.startsWith("quiz/levels") -> Quiz.Levels
                route.startsWith("quiz/test") -> Quiz.LevelTest
                route.startsWith("quiz/categories") -> QuizCategories
                route.startsWith("quiz/tests") -> QuizTests
                route.startsWith("quiz/exam") -> QuizExam
                route == "phrases" -> Phrases
                route == "dictionary" -> Dictionary
                route.startsWith("history") -> History
                route == "bookmarks" -> Bookmarks
                route == "onboarding" -> Onboarding
                route == "wordcraft" -> WordCraft
                route == "quote" -> Quote
                else -> Translation
            }
        }
    }
}