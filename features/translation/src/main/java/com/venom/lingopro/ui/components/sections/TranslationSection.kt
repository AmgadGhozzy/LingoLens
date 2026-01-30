package com.venom.lingopro.ui.components.sections

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.lingopro.ui.viewmodel.TranslationActions
import com.venom.ui.components.bars.SourceTextActionBar
import com.venom.ui.components.bars.TranslatedTextActionBar
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.components.other.GradientGlassCard
import com.venom.ui.screen.langselector.LangSelectorViewModel
import com.venom.ui.screen.langselector.LanguageBar
import com.venom.utils.Extensions.getSelectedOrFullText
import com.venom.utils.Extensions.isValidWordForSentences
import com.venom.utils.Extensions.showToast

@Composable
fun TranslationSection(
    viewModel: LangSelectorViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    sourceTextValue: TextFieldValue,
    translatedTextValue: TextFieldValue,
    actions: TranslationActions = TranslationActions.Empty,
    isLoading: Boolean = true,
    isSpeaking: Boolean = false,
    isBookmarked: Boolean = true,
    showNativeNameHint: Boolean = false,
    showFlag: Boolean = false,
    backgroundAlpha: Float = 0.1f
) {
    val context = LocalContext.current
    val sourceText = sourceTextValue.getSelectedOrFullText()
    val translatedText = translatedTextValue.getSelectedOrFullText()

    // Validation helper
    val validateAction: (() -> Unit) -> Unit = { action ->
        if (sourceTextValue.text.isNotBlank()) action()
        else context.showToast("No translation")
    }

    GradientGlassCard(
        thickness = when {
            backgroundAlpha < 0.1f -> GlassThickness.UltraThin
            else -> GlassThickness.UltraThick
        },
        contentPadding = 12.dp
    ) {
        // Language selector
        LanguageBar(
            langSelectorViewModel = viewModel,
            showNativeNameHint = showNativeNameHint,
            showFlag = showFlag
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.outline.copy(0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Source text section
        SourceTextSection(
            sourceTextValue = sourceTextValue,
            onTextChange = actions.onTextChange,
            onClearText = actions.onClearText,
            isLoading = isLoading
        )

        // Source actions
        SourceTextActionBar(
            onSpeechToTextStart = actions.onSpeechToTextStart,
            onSpeechToTextEnd = actions.onSpeechToTextEnd,
            isSpeechToTextActive = actions.isSpeechToTextActive,
            onOcr = actions.onOcr,
            onPaste = actions.onPaste,
            onCopy = { validateAction { actions.onCopy(sourceText) } },
            onFullscreen = { actions.onFullscreen(sourceText) },
            onSpeak = { validateAction { actions.onSpeak(sourceText) } },
            onSentenceExplorer = sourceTextValue.text
                .takeIf { isValidWordForSentences(it) }
                ?.let { { validateAction { actions.onSentenceExplorer?.invoke(sourceText) } } },
            isSpeaking = isSpeaking
        )

        // Translated text section
        TranslatedTextSection(
            translatedTextValue = translatedTextValue,
            isLoading = isLoading
        )

        // Translated actions
        TranslatedTextActionBar(
            onBookmark = { validateAction(actions.onBookmark) },
            onCopy = { validateAction { actions.onCopy(translatedText) } },
            onShare = { validateAction { actions.onShare(translatedText) } },
            onFullscreen = { validateAction { actions.onFullscreen(translatedText) } },
            onSpeak = { validateAction { actions.onSpeak(translatedText) } },
            onMoveUp = actions.onMoveUp,
            isSaved = isBookmarked,
            isSpeaking = isSpeaking
        )
    }
}