package com.venom.ui.navigation

sealed class Screen(val route: String) {
    object Translation : Screen("translation")
    object Ocr : Screen("ocr")
    object Dialog : Screen("dialog")
    object StackCard : Screen("stackcard")
    object Phrases : Screen("phrases")

    object Dictionary : Screen("dictionary")
    object History : Screen("history")
    object Bookmarks : Screen("bookmarks")
}