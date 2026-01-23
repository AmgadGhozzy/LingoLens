package com.venom.lingolens.navigation

import com.venom.resources.R
import com.venom.ui.navigation.Screen

enum class NavigationItems(
    val screen: Screen,
    val icon: Int,
    val titleRes: Int,
    val showInBottomBar: Boolean = true
) {
    DIALOG(Screen.Dialog, R.drawable.icon_dialogs_activated, R.string.dialog_title, false),
    QUOTE(Screen.Quote, R.drawable.icon_quotes, R.string.quote_title),
    PHRASE(Screen.Phrases, R.drawable.icon_dialog, R.string.phrase_title),
    QUIZ(Screen.Quiz, R.drawable.quiz_logo, R.string.quiz_title),
    WORDCARD(Screen.StackCard, R.drawable.icon_cards1, R.string.stackcard_title),
    OCR(Screen.Ocr, R.drawable.icon_camera, R.string.ocr_title),
    TRANSLATE(Screen.Translation, R.drawable.icon_translate, R.string.translate_title);
}