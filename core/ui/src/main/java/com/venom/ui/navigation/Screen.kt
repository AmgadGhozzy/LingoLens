package com.venom.ui.navigation

import com.venom.domain.model.WordLevels

sealed class Screen(val route: String) {
    object Translation : Screen("translation/{text}") {
        fun createRoute(text: String? = null) = "translation/${text ?: ""}"
    }

    object Ocr : Screen("ocr")
    object Dialog : Screen("dialog")
    object StackCard : Screen("stackcard/{level}") {
        fun createRoute(level: WordLevels? = null) = "stackcard/${level?.id ?: ""}"
    }

    object Phrases : Screen("phrases")
    object Dictionary : Screen("dictionary")
    object History : Screen("history")
    object Bookmarks : Screen("bookmarks")

    // Quiz screens
    sealed class Quiz(route: String) : Screen(route) {
        object MainLevel : Quiz("quiz/main")
        object LevelTest : Quiz("quiz/test/{level}")

        companion object {
            fun createRoute(level: WordLevels) = "quiz/test/${level.id}"
        }
    }

    companion object {
        fun fromRoute(route: String): Screen {
            return when {
                route.startsWith("translation") -> Translation
                route == "ocr" -> Ocr
                route == "dialog" -> Dialog
                route.startsWith("stackcard") -> StackCard
                route.startsWith("quiz/main") -> Quiz.MainLevel
                route.startsWith("quiz/test") -> Quiz.LevelTest
                route == "phrases" -> Phrases
                route == "dictionary" -> Dictionary
                route == "history" -> History
                route == "bookmarks" -> Bookmarks
                else -> Translation
            }
        }
    }
}