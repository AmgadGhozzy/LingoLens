package com.venom.lingopro.ui.components.sections

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.lingopro.ui.viewmodel.TranslationActions
import com.venom.ui.components.bars.*
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.viewmodel.LangSelectorViewModel
import com.venom.utils.Extensions.getSelectedOrFullText

@Composable
fun TranslationSection(
    viewModel: LangSelectorViewModel = hiltViewModel(),
    sourceTextValue: TextFieldValue,
    translatedTextValue: TextFieldValue,
    actions: TranslationActions = TranslationActions.Empty,
    isLoading: Boolean = true,
    isSpeaking: Boolean = false,
    isBookmarked: Boolean = false,
    showNativeNameHint: Boolean = false,
    showFlag: Boolean = false
) {
    CustomCard {
        TranslationHeader(
            viewModel = viewModel, showNativeNameHint = showNativeNameHint, showFlag = showFlag
        )

        TranslationContent(
            sourceTextValue = sourceTextValue,
            translatedTextValue = translatedTextValue,
            actions = actions,
            states = TranslationStates(
                isLoading = isLoading, isSpeaking = isSpeaking, isSaved = isBookmarked
            )
        )
    }
}

@Composable
private fun TranslationHeader(
    viewModel: LangSelectorViewModel, showNativeNameHint: Boolean, showFlag: Boolean
) {
    LanguageBar(
        viewModel = viewModel,
        showNativeNameHint = showNativeNameHint,
        showFlag = showFlag,
    )
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp
    )
}

@Composable
private fun TranslationContent(
    sourceTextValue: TextFieldValue,
    translatedTextValue: TextFieldValue,
    actions: TranslationActions,
    states: TranslationStates
) {
    // Source text section
    SourceTextSection(
        sourceTextValue = sourceTextValue,
        onTextChange = actions.onTextChange,
        onClearText = actions.onClearText,
        isLoading = states.isLoading
    )

    SourceTextActionBar(
        onSpeechToText = actions.onSpeechToText,
        onOcr = actions.onOcr,
        onPaste = actions.onPaste,
        onCopy = { actions.onCopy(sourceTextValue.getSelectedOrFullText()) },
        onFullscreen = { actions.onFullscreen(sourceTextValue.getSelectedOrFullText()) },
        onSpeak = { actions.onSpeak(sourceTextValue.getSelectedOrFullText()) },
        isSpeaking = states.isSpeaking
    )

    // Translation text section
    TranslatedTextSection(translatedTextValue = translatedTextValue)

    TranslatedTextActionBar(
        onBookmark = actions.onBookmark,
        onCopy = { actions.onCopy(translatedTextValue.getSelectedOrFullText()) },
        onShare = { actions.onShare(translatedTextValue.getSelectedOrFullText()) },
        onFullscreen = { actions.onFullscreen(translatedTextValue.getSelectedOrFullText()) },
        onSpeak = { actions.onSpeak(translatedTextValue.getSelectedOrFullText()) },
        isSaved = states.isSaved,
        isSpeaking = states.isSpeaking,
    )
}

private data class TranslationStates(
    val isLoading: Boolean = true, val isSpeaking: Boolean = false, val isSaved: Boolean = false
)

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun TranslationSectionWithoutFlagPreview() {
    LingoLensTheme {
        TranslationSection(
            sourceTextValue = TextFieldValue("Hello"),
            translatedTextValue = TextFieldValue("مرحبا"),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TranslationSectionWithFlagPreview() {
    LingoLensTheme {
        TranslationSection(
            sourceTextValue = TextFieldValue("Hello"),
            translatedTextValue = TextFieldValue("مرحبا"),
            showFlag = true
        )
    }
}
