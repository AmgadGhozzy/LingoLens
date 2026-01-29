package com.venom.ui.screen.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.domain.model.TranslationResult
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton

@Composable
fun DictionaryScreen(
    translationResult: TranslationResult,
    modifier: Modifier = Modifier,
    onWordClick: (String) -> Unit = {},
    onSpeak: (String) -> Unit = {},
    onCopy: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    isSpeaking: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .systemBarsPadding(),
        contentPadding = PaddingValues(12.dp), // ✓ Single padding
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                CustomButton(
                    onClick = onDismiss,
                    icon = R.drawable.icon_back,
                    iconSize = 32.dp,
                    contentDescription = stringResource(R.string.action_back),
                )
            }
        }

        // Translation Card
        if (translationResult.sentences.isNotEmpty()) {
            item(key = "translation") {
                TranslationCard(
                    translationResponse = translationResult,
                    onSpeak = onSpeak,
                    onCopy = onCopy,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Definitions Card
        if (translationResult.definitionEntries.isNotEmpty()) {
            item(key = "definitions") {
                DefinitionsCard(
                    definitions = translationResult.definitionEntries,
                    onTextClick = onWordClick,
                    onSpeak = onSpeak,
                    onCopy = onCopy,
                    onShare = onShare,
                    isSpeaking = isSpeaking
                )
            }
        }

        // Translations Card
        if (translationResult.dict.isNotEmpty()) {
            item(key = "translations") {
                TranslationsCard(
                    translations = translationResult.dict,
                    onWordClick = onWordClick,
                    onSpeak = onSpeak
                )
            }
        }

        // Synonyms Card
        if (translationResult.synsets.isNotEmpty()) {
            item(key = "synonyms") {
                SynonymsCard(
                    synsets = translationResult.synsets,
                    onWordClick = onWordClick
                )
            }
        }
    }
}