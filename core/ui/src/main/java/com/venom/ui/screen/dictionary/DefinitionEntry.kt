package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.domain.model.DefinitionEntry

@Composable
fun DefinitionEntry(
    entry: DefinitionEntry,
    onTextClick: ((String) -> Unit)? = null,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    isSpeaking: Boolean = false,
    modifier: Modifier = Modifier
) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlossRow(
                text = entry.gloss,
                onTextClick = onTextClick,
                onSpeak = onSpeak,
                onCopy = onCopy,
                onShare = onShare,
                isSpeaking = isSpeaking,
            )

            entry.example?.let { example ->
                ExampleRow(
                    text = example,
                    onTextClick = onTextClick,
                    onSpeak = onSpeak,
                    onCopy = onCopy,
                    onShare = onShare,
                    isSpeaking = isSpeaking
                )
            }
        }
    }
}

