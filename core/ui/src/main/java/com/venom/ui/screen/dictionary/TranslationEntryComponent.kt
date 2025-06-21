package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.domain.model.DictionaryEntry
import com.venom.ui.components.items.WordChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslationEntryComponent(
    entry: DictionaryEntry,
    showAll: Boolean,
    onWordClick: (String) -> Unit,
    onSpeak: (String) -> Unit,
    toggleShowAll: () -> Unit,
    isAlpha: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = entry.pos.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.semantics {
                contentDescription = "${entry.pos} Translation"
            }
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val visibleTerms = if (showAll) entry.terms else entry.terms.take(10)
            visibleTerms.forEach { term ->
                WordChip(
                    word = term,
                    onClick = { onWordClick(term) },
                    isAlpha = isAlpha
                )
            }

            if (entry.terms.size > 10) {
                WordChip(word = "...", onClick = toggleShowAll)
            }
        }
    }
}
