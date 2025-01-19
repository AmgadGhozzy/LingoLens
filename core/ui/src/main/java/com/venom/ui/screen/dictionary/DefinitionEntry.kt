package com.venom.ui.screen.dictionary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.venom.data.model.DefinitionEntry
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton

@Composable
fun DefinitionEntry(
    entry: DefinitionEntry,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onTextClick: ((String) -> Unit)? = null,
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
            // Main Gloss Row
            GlossRow(
                text = entry.gloss, onSpeak = onSpeak, onCopy = onCopy, onTextClick = onTextClick
            )

            // Example Row (if available)
            entry.example?.let { example ->
                ExampleRow(
                    text = example,
                    onSpeak = onSpeak,
                    onCopy = onCopy,
                    onTextClick = onTextClick,
                    isSpeaking = isSpeaking
                )
            }
        }
    }
}

@Composable
private fun GlossRow(
    text: String,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onTextClick: ((String) -> Unit)?
) {
    var isClicked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectionContainer {
                Text(text = "● $text",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = if (isClicked) TextDecoration.Underline else TextDecoration.None,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isClicked = !isClicked
                            onTextClick?.invoke(text)
                        })
            }
        }

        if (isClicked) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomButton(
                    onClick = { onSpeak(text) },
                    icon = R.drawable.icon_sound,
                    contentDescription = stringResource(R.string.action_speak)
                )
                Spacer(modifier = Modifier.width(8.dp))
                CustomButton(
                    onClick = { onCopy(text) },
                    icon = R.drawable.icon_copy,
                    contentDescription = stringResource(R.string.action_copy)
                )
            }
        }
    }
}

@Composable
private fun ExampleRow(
    text: String,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onTextClick: ((String) -> Unit)?,
    isSpeaking: Boolean
) {
    var isClicked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SelectionContainer {
            Text(text = "\"$text\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = if (isClicked) TextDecoration.Underline else TextDecoration.None,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isClicked = !isClicked
                        onTextClick?.invoke(text)
                    })
        }

        if (isClicked) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomButton(
                    onClick = { onSpeak(text) },
                    icon = if (isSpeaking) R.drawable.icon_record else R.drawable.icon_sound,
                    contentDescription = stringResource(R.string.action_speak)
                )
                Spacer(modifier = Modifier.width(8.dp))
                CustomButton(
                    onClick = { onCopy(text) },
                    icon = R.drawable.icon_copy,
                    contentDescription = stringResource(R.string.action_copy)
                )
            }
        }
    }
}
