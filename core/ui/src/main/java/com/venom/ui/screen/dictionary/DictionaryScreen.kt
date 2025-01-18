package com.venom.ui.screen.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.venom.data.mock.TRANSLATION_RESPONSE
import com.venom.data.model.TranslationResponse

@Composable
fun DictionaryScreen(
    translationResponse: TranslationResponse,
    modifier: Modifier = Modifier,
    onWordClick: (String) -> Unit = {},
    onSpeak: (String) -> Unit = {},
    onCopy: (String) -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Card displaying translation details
        TranslationCard(
            translationResponse = translationResponse,
            onSpeak = onSpeak,
            onCopy = onCopy,
            modifier = Modifier.fillMaxWidth()
        )

        // Display translations if available
        translationResponse.dict?.let { translations ->
            TranslationsCard(
                translations = translations,
                onWordClick = onWordClick,
                onSpeak = onSpeak,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Display definitions if available
        translationResponse.definitions?.let { definitions ->
            DefinitionsCard(
                definitions = definitions,
                onSpeak = onSpeak,
                onCopy = onCopy,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Display synonyms if available
        translationResponse.synsets?.let { synsets ->
            SynonymsCard(
                synsets = synsets,
                onWordClick = onWordClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun PreviewDictionaryScreen() {
    val translationResponse = Gson().fromJson(TRANSLATION_RESPONSE, TranslationResponse::class.java)
    DictionaryScreen(translationResponse = translationResponse)
}
