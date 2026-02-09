package com.venom.ui.screen.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.venom.domain.model.TranslationResult
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp

@Composable
fun DictionaryScreen(
    translationResult: TranslationResult,
    modifier: Modifier = Modifier,
    onWordClick: (String) -> Unit = {},
    onSpeak: (String) -> Unit = {},
    onCopy: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    isSpeakingText: (String) -> Boolean = { false },
    onDismiss: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .systemBarsPadding(),
        contentPadding = PaddingValues(12.adp),
        verticalArrangement = Arrangement.spacedBy(16.adp)
    ) {
        item(key = "header") {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 6.adp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.title_dictionary),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.asp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                CustomButton(
                    onClick = onDismiss,
                    icon = R.drawable.icon_back,
                    iconSize = 32.adp,
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
                    isSpeakingText = isSpeakingText
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