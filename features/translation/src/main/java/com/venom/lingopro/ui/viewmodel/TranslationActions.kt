package com.venom.lingopro.ui.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import com.venom.data.model.TranslationProvider

data class TranslationActions(
    val onTextChange: (TextFieldValue) -> Unit,
    val onClearText: () -> Unit,
    val onCopy: (String) -> Unit,
    val onShare: (String) -> Unit,
    val onSpeak: (String) -> Unit,
    val onPaste: () -> Unit,
    val onMoveUp: () -> Unit,
    val onOcr: () -> Unit,
    val onBookmark: () -> Unit,
    val onSpeechToText: () -> Unit,
    val onFullscreen: (String) -> Unit,
    val onProviderChange: (TranslationProvider) -> Unit = {}
) {
    companion object {
        val Empty = TranslationActions(onTextChange = {},
            onClearText = {},
            onCopy = {},
            onShare = {},
            onSpeak = {},
            onPaste = {},
            onMoveUp = {},
            onOcr = {},
            onBookmark = {},
            onSpeechToText = {},
            onFullscreen = {})
    }
}
