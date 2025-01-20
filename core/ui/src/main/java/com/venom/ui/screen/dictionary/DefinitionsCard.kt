package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.data.model.*
import com.venom.resources.R
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.components.items.WordChip

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

    CustomCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader(title = stringResource(id = R.string.definitions), icon = {
                Icon(
                    imageVector = Icons.Rounded.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            })

            val visibleDefinitions = if (showAll) definitions else definitions.take(5)
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

            if (definitions.size > 5) {
                WordChip(word = if (showAll) stringResource(id = R.string.show_less)
                else stringResource(id = R.string.show_more), onClick = { showAll = !showAll })
            }
        }
    }
}

