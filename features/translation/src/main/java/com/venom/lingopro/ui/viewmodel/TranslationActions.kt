package com.venom.lingopro.ui.viewmodel

import androidx.compose.ui.text.input.TextFieldValue

data class TranslationActions(
    val onTextChange: (TextFieldValue) -> Unit,
    val onClearText: () -> Unit,
    val onCopy: (String) -> Unit,
    val onShare: (String) -> Unit,
    val onSpeak: (String) -> Unit,
    val onPaste: () -> Unit,
    val onOcr: () -> Unit,
    val onBookmark: () -> Unit,
    val onSpeechToText: () -> Unit,
    val onFullscreen: (String) -> Unit
) {
    companion object {
        val Empty = TranslationActions(onTextChange = {},
            onClearText = {},
            onCopy = {},
            onShare = {},
            onSpeak = {},
            onPaste = {},
            onOcr = {},
            onBookmark = {},
            onSpeechToText = {},
            onFullscreen = {})
    }
}
