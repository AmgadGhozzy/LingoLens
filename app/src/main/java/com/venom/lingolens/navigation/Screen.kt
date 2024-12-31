package com.venom.lingolens.navigation

sealed class Screen(val route: String) {
    object Translation : Screen("translation")
    object Ocr : Screen("ocr")
    object Dictionary : Screen("dictionary")
    object PhrasesCategory : Screen("category/{categoryId}") {
        fun createRoute(categoryId: Int) = "category/$categoryId"
    }
    object Phrases : Screen("phrases")


    object History : Screen("history")
    object Bookmarks : Screen("bookmarks")
}