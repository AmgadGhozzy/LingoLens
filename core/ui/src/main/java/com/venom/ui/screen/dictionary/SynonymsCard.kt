package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.venom.domain.model.Synset
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.components.items.WordChip
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.components.other.GradientGlassCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SynonymsCard(
    synsets: List<Synset>,
    onWordClick: (String) -> Unit
) {
    var showAll by rememberSaveable { mutableStateOf(false) }

    GradientGlassCard(
        thickness = GlassThickness.UltraThin,
        gradientAlpha = 0.1f,
        contentPadding = 16.adp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.adp)
        ) {
            SectionHeader(title = stringResource(id = R.string.synonyms))

            synsets.forEach { synset ->
                key(synset.pos) {
                    SynsetSection(
                        synset = synset,
                        showAll = showAll,
                        onWordClick = onWordClick,
                        onToggleShowAll = { showAll = !showAll }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SynsetSection(
    synset: Synset,
    showAll: Boolean,
    onWordClick: (String) -> Unit,
    onToggleShowAll: () -> Unit
) {
    val synonyms by remember(synset) {
        derivedStateOf {
            synset.entry.flatMap { it.synonym }.distinct()
        }
    }

    val visibleSynonyms = if (showAll) synonyms else synonyms.take(15)

    Column(verticalArrangement = Arrangement.spacedBy(8.adp)) {
        Text(
            text = synset.pos.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.semantics {
                contentDescription = "${synset.pos} Synonyms"
            }
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.adp),
            verticalArrangement = Arrangement.spacedBy(8.adp)
        ) {
            visibleSynonyms.forEach { synonym ->
                key(synonym) {
                    WordChip(
                        word = synonym,
                        onClick = { onWordClick(synonym) }
                    )
                }
            }

            if (synonyms.size > 15) {
                WordChip(
                    word = if (showAll) {
                        stringResource(id = R.string.show_less)
                    } else {
                        stringResource(id = R.string.show_more)
                    },
                    onClick = onToggleShowAll
                )
            }
        }
    }
}