package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.data.model.*
import com.venom.resources.R
import com.venom.ui.components.items.WordChip

@Composable
fun DefinitionSection(
    definition: Definition,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var showAll by remember { mutableStateOf(false) }

    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = definition.pos.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onSpeak(definition.pos) }, modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Speak part of speech",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        var visibleDefinitions = if (showAll) definition.entry else definition.entry.take(5)
        visibleDefinitions.forEach { entry ->
            DefinitionEntry(
                entry = entry, onSpeak = onSpeak, onCopy = onCopy
            )
        }

        if (definition.entry.size > 5)
            WordChip(word = if (showAll) stringResource(id = R.string.show_less
        ) else stringResource(id = R.string.show_all),
            onClick = { showAll = !showAll })
    }
}
