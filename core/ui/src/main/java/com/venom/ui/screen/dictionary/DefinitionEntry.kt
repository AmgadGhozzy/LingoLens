package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.data.model.DefinitionEntry
import com.venom.resources.R
import com.venom.ui.components.buttons.CopyButton
import com.venom.ui.components.buttons.CustomButton

@Composable
fun DefinitionEntry(
    entry: DefinitionEntry,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    isSpeaking: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main Gloss Row
        GlossRow(
            text = entry.gloss, onSpeak = onSpeak, onCopy = onCopy
        )

        // Example Row (if available)
        entry.example?.let { example ->
            ExampleRow(
                text = example, onSpeak = onSpeak, onCopy = onCopy, isSpeaking = isSpeaking
            )
        }
    }
}

@Composable
private fun GlossRow(
    text: String, onSpeak: (String) -> Unit, onCopy: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "• $text",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        ActionButtons(onSpeak = { onSpeak(text) }, onCopy = { onCopy(text) })
    }
}

@Composable
private fun ExampleRow(
    text: String, onSpeak: (String) -> Unit, onCopy: (String) -> Unit, isSpeaking: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "\"$text\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CustomButton(
                onClick = { onSpeak(text) },
                icon = if (isSpeaking) R.drawable.icon_record else R.drawable.icon_sound,
                contentDescription = stringResource(R.string.action_speak)
            )
            CopyButton(onClick = { onCopy(text) })
        }
    }
}

@Composable
private fun ActionButtons(
    onSpeak: () -> Unit, onCopy: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(
            onClick = onSpeak, modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = stringResource(R.string.action_speak),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(
            onClick = onCopy, modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = stringResource(R.string.action_copy),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
