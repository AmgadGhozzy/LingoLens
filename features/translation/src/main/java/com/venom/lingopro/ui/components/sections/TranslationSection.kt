package com.venom.lingopro.ui.components.sections

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.ui.components.bars.LanguageBar
import com.venom.ui.components.bars.SourceTextActionBar
import com.venom.ui.components.bars.TranslatedTextActionBar
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.viewmodel.LangSelectorViewModel
import com.venom.utils.Extensions.getSelectedOrFullText

@Composable
fun TranslationSection(
    viewModel: LangSelectorViewModel = hiltViewModel(),
    sourceTextValue: TextFieldValue,
    translatedTextValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit = {},
    isLoading: Boolean = true,
    onClearText: () -> Unit = {},
    onCopy: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    onSpeak: (String) -> Unit = {},
    onPaste: () -> Unit = {},
    onOcr: () -> Unit = {},
    onSave: () -> Unit = {},
    onFullscreen: (String) -> Unit = {},
    onSpeechToText: () -> Unit = {},
    showNativeNameHint: Boolean = false,
    showFlag: Boolean = false
) {

    var isSaved by remember { mutableStateOf(false) }

    CustomCard {
        // Translation Top Bar for source and target languages
        LanguageBar(
            viewModel = viewModel,
            showNativeNameHint = showNativeNameHint,
            showFlag = showFlag,
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)

        // Source Text Section for user input
        SourceTextSection(
            sourceTextValue = sourceTextValue,
            onTextChange = onTextChange,
            onClearText = onClearText,
            isLoading = isLoading
        )

        // Source Action bar for text interactions
        SourceTextActionBar(
            onSpeechToText = onSpeechToText,
            onOcr = onOcr,
            onPaste = onPaste,
            onCopy = { onCopy(sourceTextValue.getSelectedOrFullText()) },
            onFullscreen = { onFullscreen(sourceTextValue.getSelectedOrFullText()) },
            onSpeak = { onSpeak(sourceTextValue.getSelectedOrFullText()) },
        )

        // Translated Text Section for translated text
        TranslatedTextSection(
            translatedTextValue = translatedTextValue
        )

        // Translated Action bar for text interactions
        TranslatedTextActionBar(
            onSave = { isSaved = !isSaved; onSave() },
            onCopy = { onCopy(translatedTextValue.getSelectedOrFullText()) },
            onShare = { onShare(translatedTextValue.getSelectedOrFullText()) },
            onFullscreen = { onFullscreen(translatedTextValue.getSelectedOrFullText()) },
            onSpeak = { onSpeak(translatedTextValue.getSelectedOrFullText()) },
        )
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TranslationSectionWithoutFlagPreview() {
    LingoLensTheme {
        TranslationSection(
            sourceTextValue = TextFieldValue("Hello"),
            translatedTextValue = TextFieldValue("مرحبا"),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TranslationSectionPreview() {
    TranslationSection(
        sourceTextValue = TextFieldValue("Hello"),
        translatedTextValue = TextFieldValue("مرحبا"),
        showFlag = true
    )
}
