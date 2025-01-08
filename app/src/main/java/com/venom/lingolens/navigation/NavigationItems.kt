package com.venom.lingolens.navigation

import com.venom.resources.R

enum class NavigationItems(
    val screen: Screen,
    val icon: Int,
    val titleRes: Int
) {
    WORDCARD(Screen.WordCard, R.drawable.icon_cards1, R.string.wordcard_title),
    DIALOG(Screen.Dialog, R.drawable.icon_dialog, R.string.dialog_title),
    PHRASE(Screen.Phrases, R.drawable.icon_dialog, R.string.phrase_title),
    OCR(Screen.Ocr, R.drawable.icon_camera, R.string.ocr_title),
    TRANSLATE(Screen.Translation, R.drawable.icon_translate, R.string.translate_title)
}