package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.venom.domain.model.DictionaryEntry
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.components.items.WordChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun  TranslationEntryComponent(
    entry: DictionaryEntry,
    showAll: Boolean,
    onWordClick: (String) -> Unit,
    onSpeak: (String) -> Unit,
    toggleShowAll: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.adp)) {
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
            horizontalArrangement = Arrangement.spacedBy(8.adp),
            verticalArrangement = Arrangement.spacedBy(8.adp)
        ) {
            val visibleTerms = if (showAll) entry.terms else entry.terms.take(8)
            visibleTerms.forEach { term ->
                WordChip(
                    word = term,
                    onClick = { onWordClick(term) }
                )
            }

            if (entry.terms.size > 8) {
                WordChip(
                    word = if (showAll) stringResource(id = R.string.show_less)
                    else stringResource(id = R.string.show_more),
                    onClick = toggleShowAll
                )
            }
        }
    }
}