package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.domain.model.Definition
import com.venom.resources.R
import com.venom.ui.components.items.WordChip
import com.venom.ui.components.other.GlassCard

@Composable
fun DefinitionsCard(
    definitions: List<Definition>,
    onTextClick: ((String) -> Unit)? = null,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    isSpeaking: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showAll by remember { mutableStateOf(false) }

    GlassCard(modifier = modifier, padding = 16.dp) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader(
                title = stringResource(id = R.string.definitions),
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            val visibleDefinitions = if (showAll) definitions else definitions.take(3)
            visibleDefinitions.forEach { definition ->
                DefinitionSection(
                    definition = definition,
                    onTextClick = onTextClick,
                    onSpeak = onSpeak,
                    onCopy = onCopy,
                    onShare = onShare,
                    isSpeaking = isSpeaking
                )
            }

            if (definitions.size > 3) {
                WordChip(
                    word = if (showAll) stringResource(id = R.string.show_less)
                    else stringResource(id = R.string.show_more),
                    onClick = { showAll = !showAll }
                )
            }
        }
    }
}