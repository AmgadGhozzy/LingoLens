package com.venom.lingopro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.venom.lingopro.ui.components.sections.TranslationSection
import com.venom.lingopro.ui.viewmodel.TranslationActions
import com.venom.ui.components.common.adp
import com.venom.ui.screen.dictionary.TranslationsCard
import com.venom.ui.screen.langselector.LangSelectorViewModel
import com.venom.ui.viewmodel.TranslateUiState

@Composable
fun TranslationContent(
    langSelectorViewModel: LangSelectorViewModel,
    state: TranslateUiState,
    sourceTextFieldValue: TextFieldValue,
    translatedTextFieldValue: TextFieldValue,
    actions: TranslationActions,
    isSpeakingText: (String) -> Boolean,
    onExpandDictionary: () -> Unit,
    onSpeakDictionaryWord: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDialog: Boolean = false
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Simple adaptive logic: If width > 600adp, use Row (Side-by-side), else Column
        val isExpanded = maxWidth > 600.adp
        
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.adp),
                horizontalArrangement = Arrangement.spacedBy(24.adp)
            ) {
                // Left Panel: Input & Actions
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    TranslationSection(
                        viewModel = langSelectorViewModel,
                        sourceTextValue = sourceTextFieldValue,
                        translatedTextValue = translatedTextFieldValue,
                        actions = actions,
                        isLoading = state.isLoading,
                        isSpeakingText = isSpeakingText,
                        isBookmarked = state.isBookmarked,
                        backgroundAlpha = if (isDialog) 1f else 0.1f
                    )
                }

                // Right Panel: Dictionary Results
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    if (state.translationResult.dict.isNotEmpty()) {
                        TranslationsCard(
                            translations = state.translationResult.dict,
                            onWordClick = { selectedWord ->
                                if (selectedWord.isNotBlank() && selectedWord != sourceTextFieldValue.text) {
                                    actions.onTextChange(TextFieldValue(selectedWord))
                                }
                            },
                            onExpand = onExpandDictionary,
                            onSpeak = onSpeakDictionaryWord,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }
        } else {
            // Compact Mode (Original Vertical Layout)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.adp)
            ) {
                TranslationSection(
                    viewModel = langSelectorViewModel,
                    sourceTextValue = sourceTextFieldValue,
                    translatedTextValue = translatedTextFieldValue,
                    actions = actions,
                    isLoading = state.isLoading,
                    isSpeakingText = isSpeakingText,
                    isBookmarked = state.isBookmarked,
                    backgroundAlpha = if (isDialog) 1f else 0.1f
                )

                if (state.translationResult.dict.isNotEmpty() && !isDialog) {
                    TranslationsCard(
                        translations = state.translationResult.dict,
                        onWordClick = { selectedWord ->
                            if (selectedWord.isNotBlank() && selectedWord != sourceTextFieldValue.text) {
                                actions.onTextChange(TextFieldValue(selectedWord))
                            }
                        },
                        onExpand = onExpandDictionary,
                        onSpeak = onSpeakDictionaryWord,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 70.adp)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}
