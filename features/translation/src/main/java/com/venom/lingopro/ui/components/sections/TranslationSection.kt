package com.venom.lingopro.ui.components.sections

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.lingopro.ui.viewmodel.TranslationActions
import com.venom.ui.components.bars.*
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.screen.langselector.LangSelectorViewModel
import com.venom.ui.screen.langselector.LanguageBar
import com.venom.ui.theme.LingoLensTheme
import com.venom.utils.Extensions.getSelectedOrFullText
import com.venom.utils.Extensions.showToast

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
    val context = LocalContext.current
    val validateAndExecute: (() -> Unit) -> Unit = {
        if (sourceTextValue.text.isBlank()) context.showToast("No translation")
        else it()
    }

    CustomCard {
        LanguageBar(
            viewModel = viewModel,
            showNativeNameHint = showNativeNameHint,
            showFlag = showFlag
        )
        
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 0.5.dp
        )
        
        SourceTextSection(
            sourceTextValue = sourceTextValue,
            onTextChange = actions.onTextChange,
            onClearText = actions.onClearText,
            isLoading = isLoading
        )

        SourceTextActionBar(
            onSpeechToText = actions.onSpeechToText,
            onOcr = actions.onOcr,
            onPaste = actions.onPaste,
            onCopy = { validateAndExecute { actions.onCopy(sourceTextValue.getSelectedOrFullText()) } },
            onFullscreen = { validateAndExecute { actions.onFullscreen(sourceTextValue.getSelectedOrFullText()) } },
            onSpeak = { validateAndExecute { actions.onSpeak(sourceTextValue.getSelectedOrFullText()) } },
            isSpeaking = isSpeaking
        )

        TranslatedTextSection(translatedTextValue = translatedTextValue)

        TranslatedTextActionBar(
            onBookmark = { validateAndExecute(actions.onBookmark) },
            onCopy = { validateAndExecute { actions.onCopy(translatedTextValue.getSelectedOrFullText()) } },
            onShare = { validateAndExecute { actions.onShare(translatedTextValue.getSelectedOrFullText()) } },
            onFullscreen = { validateAndExecute { actions.onFullscreen(translatedTextValue.getSelectedOrFullText()) } },
            onSpeak = { validateAndExecute { actions.onSpeak(translatedTextValue.getSelectedOrFullText()) } },
            onMoveUp = actions.onMoveUp,
            isSaved = isBookmarked,
            isSpeaking = isSpeaking
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun TranslationSectionPreview() {
    LingoLensTheme {
        TranslationSection(
            sourceTextValue = TextFieldValue("Hello"),
            translatedTextValue = TextFieldValue("مرحبا")
        )
    }
}
