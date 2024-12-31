package com.venom.lingolens.navigation

import com.venom.resources.R
enum class NavigationItems(
    val screen: Screen,
    val icon: Int,
    val titleRes: Int
) {
    BOOKMARKS(Screen.Phrases, R.drawable.icon_dialog, R.string.phrase_title),
    OCR(Screen.Ocr, R.drawable.icon_camera, R.string.ocr_title),
    TRANSLATE(Screen.Translation, R.drawable.icon_translate, R.string.translate_title)
}