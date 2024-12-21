package com.venom.lingolens.navigation

sealed class Screen(val route: String) {
    object Translation : Screen("translation")
    object Ocr : Screen("ocr")
    object Dictionary : Screen("dictionary")
    object Phrases : Screen("phrases")
    object Bookmarks : Screen("bookmarks")
}