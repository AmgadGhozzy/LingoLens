package com.venom.ui.components.items

import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WordChip(
    word: String,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    SuggestionChip(
        onClick = onClick, modifier = modifier.then(
            if (onLongClick != null)
                Modifier.combinedClickable(
                    onClick = onClick, onLongClick = onLongClick
                )
            else
                Modifier
        ), label = {
            Text(
                text = word,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(
                    shadow = Shadow(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
        }, colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        ), border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = true, borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    )
}

@Preview
@Composable
private fun WordChipPreview() {
    WordChip(word = "Example", onClick = {})
}
