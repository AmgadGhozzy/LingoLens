package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.domain.model.TranslationResult
import com.venom.stackcard.data.model.WordEntity
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.common.ExpandableCard
import com.venom.ui.screen.dictionary.TranslationEntryComponent
import com.venom.ui.viewmodel.TranslateViewModel

@Composable
fun BookmarkWordItem(
    word: WordEntity,
    translateViewModel: TranslateViewModel = hiltViewModel(),
    showAll: Boolean,
    onBookmark: () -> Unit,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val state by translateViewModel.uiState.collectAsStateWithLifecycle()

    val translations = remember(word.englishEn) {
        mutableStateOf<TranslationResult?>(null)
    }

    // Update the item translation state when the global state changes and this item is expanded
    LaunchedEffect(state.translationResult, expanded) {
        if (expanded && word.englishEn == state.sourceText) {
            translations.value = state.translationResult
        }
    }

    // Trigger translation when expanded
    LaunchedEffect(expanded) {
        if (expanded && translations.value == null) {
            translateViewModel.onSourceTextChanged(word.englishEn)
        }
    }

    ExpandableCard(
        title = word.englishEn,
        onSpeak = onSpeak,
        onCopy = onCopy,
        onBookmark = onBookmark,
        onExpandChange = {
            expanded = it
            if (expanded && translations.value == null) {
                translateViewModel.onSourceTextChanged(word.englishEn)
            }
        },
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
                translations.value?.dict?.forEach { entry ->
                    TranslationEntryComponent(
                        entry = entry,
                        showAll = showAll,
                        onWordClick = {},
                        onSpeak = onSpeak,
                        toggleShowAll = {},
                        isAlpha = true
                    )
                }
            }
        }
    )
}
