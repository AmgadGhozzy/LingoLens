package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
        backgroundColor = MaterialTheme.colorScheme.primary.copy(0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
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