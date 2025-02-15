package com.venom.lingolens.navigation

import com.venom.resources.R
import com.venom.ui.navigation.Screen


enum class NavigationItems(
    val screen: Screen,
    val icon: Int,
    val titleRes: Int,
    val showInBottomBar: Boolean = true
) {
    WORDCARD(Screen.StackCard, R.drawable.icon_cards1, R.string.stackcard_title),
    QUIZ(Screen.Quiz.MainLevel, R.drawable.icon_dialog, R.string.quiz_title),
    DIALOG(Screen.Dialog, R.drawable.icon_dialogs_activated, R.string.dialog_title, false),
    PHRASE(Screen.Phrases, R.drawable.icon_dialog, R.string.phrase_title),
    OCR(Screen.Ocr, R.drawable.icon_camera, R.string.ocr_title),
    TRANSLATE(Screen.Translation, R.drawable.icon_translate, R.string.translate_title);
}