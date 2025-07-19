package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.domain.model.Definition
import com.venom.resources.R
import com.venom.ui.components.items.WordChip

@Composable
fun DefinitionSection(
    definition: Definition,
    onTextClick: ((String) -> Unit)? = null,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    isSpeaking: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showAll by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = definition.pos.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.semantics {
                    contentDescription = "${definition.pos} Definition"
                }
            )
        }

        val visibleDefinitions = if (showAll) definition.entry else definition.entry.take(3)
        visibleDefinitions.forEach { entry ->
            DefinitionEntry(
                entry = entry,
                onTextClick = onTextClick,
                onSpeak = onSpeak,
                onCopy = onCopy,
                onShare = onShare,
                isSpeaking = isSpeaking
            )
        }

        if (definition.entry.size > 3) {
            WordChip(
                word = if (showAll) stringResource(id = R.string.show_less)
                else stringResource(id = R.string.show_more),
                onClick = { showAll = !showAll }
            )
        }
    }
}