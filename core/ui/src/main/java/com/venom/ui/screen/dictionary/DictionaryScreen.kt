package com.venom.ui.screen.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.domain.model.TranslationResult
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton

@Composable
fun DictionaryScreen(
    translationResponse: TranslationResult,
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

        TranslationCard(
            translationResponse = translationResponse,
            onSpeak = onSpeak,
            onCopy = onCopy,
            modifier = Modifier.fillMaxWidth()
        )

        if (translationResponse.definitionEntries.isNotEmpty()) {
            DefinitionsCard(
                definitions = translationResponse.definitionEntries,
                onTextClick = onWordClick,
                onSpeak = onSpeak,
                onCopy = onCopy,
                onShare = onShare,
                isSpeaking = isSpeaking,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (translationResponse.dict.isNotEmpty()) {
            TranslationsCard(
                translations = translationResponse.dict,
                onWordClick = onWordClick,
                onSpeak = onSpeak,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (translationResponse.synsets.isNotEmpty()) {
            SynonymsCard(
                synsets = translationResponse.synsets,
                onWordClick = onWordClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}