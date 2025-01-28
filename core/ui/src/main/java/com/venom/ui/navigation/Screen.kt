package com.venom.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Translation : Screen("translation") {
        fun createRoute(text: String? = null): String =
            if (text.isNullOrEmpty()) route
            else "$route/${Uri.encode(text)}"
    }
    object Ocr : Screen("ocr")
    object Dialog : Screen("dialog")
    object StackCard : Screen("stackcard")
    object Phrases : Screen("phrases")
    object Dictionary : Screen("dictionary")
    object History : Screen("history")
    object Bookmarks : Screen("bookmarks")

    companion object {
        fun fromRoute(route: String): Screen {
            return when {
                route.startsWith("translation") -> Translation
                route == "ocr" -> Ocr
                route == "dialog" -> Dialog
                route == "stackcard" -> StackCard
                route == "phrases" -> Phrases
                route == "dictionary" -> Dictionary
                route == "history" -> History
                route == "bookmarks" -> Bookmarks
                else -> Translation
            }
        }
    }
}