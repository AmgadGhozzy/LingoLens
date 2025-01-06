package com.venom.wordcard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.wordcard.data.model.WordCardModel

@Composable
fun ActionButtons(
    card: WordCardModel, onFlip: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                icon = Icons.Rounded.ContentCopy,
                contentDescription = "Copy word ${card.word}",
                backgroundColor = MaterialTheme.colorScheme.primary,
                iconTint = MaterialTheme.colorScheme.onPrimary
            )

            ActionButton(
                icon = Icons.Rounded.Refresh,
                contentDescription = "Flip card for ${card.word}",
                backgroundColor = MaterialTheme.colorScheme.secondary,
                iconTint = MaterialTheme.colorScheme.onSecondary,
                onClick = onFlip
            )

            ActionButton(
                icon = Icons.Rounded.Mic,
                contentDescription = "Speech-to-text for ${card.word}",
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                iconTint = MaterialTheme.colorScheme.onTertiary
            )
        }

        BookmarkButton(card)
    }
}
