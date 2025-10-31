package com.venom.lingopro.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.venom.data.model.TranslationProvider

@Immutable
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
    val onSpeechToTextStart: () -> Unit,
    val onSpeechToTextEnd: () -> Unit,
    val isSpeechToTextActive: Boolean,
    val onFullscreen: (String) -> Unit,
    val onSentenceExplorer: ((String) -> Unit)? = null,
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
            onSpeechToTextStart = {},
            onSpeechToTextEnd = {},
            isSpeechToTextActive = false,
            onFullscreen = {})
    }
}
