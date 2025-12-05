package com.venom.stackcard.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.domain.model.TranslationResult
import com.venom.domain.model.Word
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.common.ExpandableCard
import com.venom.ui.screen.dictionary.TranslationEntryComponent
import com.venom.ui.viewmodel.TranslateViewModel

@Composable
fun BookmarkWordItem(
    word: Word,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    showAll: Boolean,
    onBookmark: () -> Unit,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
    translateViewModel: TranslateViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    val state by translateViewModel.uiState.collectAsStateWithLifecycle()
    var translations by remember { mutableStateOf<TranslationResult?>(null) }

    // Load translations when expanded
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            // Check if we already have translations for this word
            if (word.englishEn == state.sourceText) {
                translations = state.translationResult
            } else {
                // Request new translation
                translateViewModel.onSourceTextChanged(word.englishEn)
            }
        }
    }

    LaunchedEffect(state.translationResult) {
        if (isExpanded && word.englishEn == state.sourceText) {
            translations = state.translationResult
        }
    }

    ExpandableCard(
        title = word.englishEn,
        expanded = isExpanded,
        onExpandChange = onExpandChange,
        onSpeak = { onSpeak(word.englishEn) },
        onCopy = { onCopy(word.englishEn) },
        onBookmark = onBookmark,
        modifier = modifier,
        expandedContent = {
            DynamicStyledText(
                text = word.arabicAr,
                maxFontSize = 18,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
            )

            if (word.synonyms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Synonyms: ${word.synonyms.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                translations?.dict?.forEach { entry ->
                    TranslationEntryComponent(
                        entry = entry,
                        showAll = showAll,
                        onWordClick = {},
                        onSpeak = { onSpeak(it) },
                        toggleShowAll = {}
                    )
                }
            }
        }
    )
}