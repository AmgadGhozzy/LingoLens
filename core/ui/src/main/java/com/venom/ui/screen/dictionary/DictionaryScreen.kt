package com.venom.ui.screen.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
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

        // Translation Card - always shown if sentences exist
        if (translationResult.sentences.isNotEmpty()) {
            TranslationCard(
                translationResponse = translationResult,
                onSpeak = onSpeak,
                onCopy = onCopy,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (translationResult.definitionEntries.isNotEmpty()) {
            DefinitionsCard(
                definitions = translationResult.definitionEntries,
                onTextClick = onWordClick,
                onSpeak = onSpeak,
                onCopy = onCopy,
                onShare = onShare,
                isSpeaking = isSpeaking,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (translationResult.dict.isNotEmpty()) {
            TranslationsCard(
                translations = translationResult.dict,
                onWordClick = onWordClick,
                onSpeak = onSpeak,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (translationResult.synsets.isNotEmpty()) {
            SynonymsCard(
                synsets = translationResult.synsets,
                onWordClick = onWordClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}