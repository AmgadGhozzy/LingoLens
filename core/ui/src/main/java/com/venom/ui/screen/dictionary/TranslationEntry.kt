package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.data.model.DictionaryEntry
import com.venom.ui.components.items.WordChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslationEntry(
    entry: DictionaryEntry,
    showAll: Boolean,
    onWordClick: (String) -> Unit,
    onSpeak: (String) -> Unit,
    toggleShowAll: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Display part of speech
        Text(
            text = entry.pos.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )

        // Display terms as chips, with a toggle for showing all or a subset
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val visibleTerms = if (showAll) entry.terms else entry.terms.take(10)
            visibleTerms.forEach { term ->
                WordChip(word = term,
                    onClick = { onWordClick(term) },
                    onLongClick = { onSpeak(term) })
            }

            // Add "..." chip if there are more terms to show
            if (entry.terms.size > 10) WordChip(word = "...", onClick = toggleShowAll)
        }
    }
}
