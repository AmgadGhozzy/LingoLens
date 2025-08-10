package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.domain.model.Synset
import com.venom.resources.R
import com.venom.ui.components.items.WordChip
import com.venom.ui.components.other.GlassCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SynonymsCard(
    synsets: List<Synset>,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAll by remember { mutableStateOf(false) }

    GlassCard(modifier = modifier, padding = 16.dp) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader(
                title = stringResource(id = R.string.synonyms),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.CompareArrows,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            synsets.forEach { synset ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val synonyms = synset.entry.flatMap { it.synonym }.distinct()
                        val visibleSynonyms = if (showAll) synonyms else synonyms.take(15)
                        visibleSynonyms.forEach { synonym ->
                            WordChip(
                                word = synonym,
                                onClick = { onWordClick(synonym) }
                            )
                        }

                        if (synonyms.size > 15) {
                            WordChip(
                                word = if (showAll) stringResource(id = R.string.show_less)
                                else stringResource(id = R.string.show_more),
                                onClick = { showAll = !showAll }
                            )
                        }
                    }
                }
            }
        }
    }
}