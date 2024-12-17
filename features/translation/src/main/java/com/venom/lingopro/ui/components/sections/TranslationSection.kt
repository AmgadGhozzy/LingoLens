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
import com.venom.lingopro.domain.model.LanguageItem
import com.venom.lingopro.ui.components.bars.LanguageBar
import com.venom.lingopro.ui.components.bars.SourceTextActionBar
import com.venom.lingopro.ui.components.bars.TranslatedTextActionBar
import com.venom.lingopro.ui.components.dialogs.CustomCard
import com.venom.lingopro.ui.theme.LingoProTheme
import com.venom.lingopro.utils.Constants.LANGUAGES_LIST
import com.venom.lingopro.utils.Extensions.getSelectedOrFullText

@Composable
fun TranslationSection(
    sourceLang: LanguageItem = LANGUAGES_LIST[0],
    targetLang: LanguageItem = LANGUAGES_LIST[1],
    sourceTextValue: TextFieldValue,
    translatedTextValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit = {},
    isLoading: Boolean = true,
    onLanguageSelect: (Boolean, LanguageItem) -> Unit = { _, _ -> },
    onSwapLanguages: () -> Unit = {},
    onClearText: () -> Unit = {},
    onCopy: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    onSpeak: (String) -> Unit = {},
    onPaste: () -> Unit = {},
    onOcr: () -> Unit = {},
    onSave: () -> Unit = {},
    onFullscreen: () -> Unit = {},
    onSpeechToText: () -> Unit = {},
    showNativeNameHint: Boolean = false,
    showFlag: Boolean = false
) {

    var isSaved by remember { mutableStateOf(false) }

    CustomCard {
        // Translation Top Bar for source and target languages
        LanguageBar(
            sourceLang = sourceLang,
            targetLang = targetLang,
            onLanguageSelect = onLanguageSelect,
            onSwapLanguages = onSwapLanguages,
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
            onFullscreen = {},
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
            onFullscreen = onFullscreen,
            onSpeak = { onSpeak(translatedTextValue.getSelectedOrFullText()) },
        )
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TranslationSectionWithoutFlagPreview() {
    LingoProTheme {
        TranslationSection(
            sourceLang = LANGUAGES_LIST[0],
            targetLang = LANGUAGES_LIST[1],
            sourceTextValue = TextFieldValue("Hello"),
            translatedTextValue = TextFieldValue("مرحبا"),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TranslationSectionPreview() {
    TranslationSection(
        sourceLang = LANGUAGES_LIST[0],
        targetLang = LANGUAGES_LIST[1],
        sourceTextValue = TextFieldValue("Hello"),
        translatedTextValue = TextFieldValue("مرحبا"),
        showFlag = true
    )
}
