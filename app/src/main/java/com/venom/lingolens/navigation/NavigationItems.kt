package com.venom.lingolens.navigation

import com.venom.resources.R
enum class NavigationItems(
    val screen: Screen,
    val icon: Int,
    val titleRes: Int
) {
    BOOKMARKS(Screen.Bookmarks, R.drawable.icon_bookmark_filled, R.string.bookmarks_title),
    OCR(Screen.Ocr, R.drawable.icon_camera, R.string.ocr_title),
    TRANSLATE(Screen.Translation, R.drawable.icon_translate, R.string.translate_title)
}