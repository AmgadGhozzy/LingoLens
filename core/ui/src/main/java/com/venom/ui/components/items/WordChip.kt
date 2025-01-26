package com.venom.ui.components.items

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.venom.ui.theme.LingoLensTheme


@Composable
fun WordChip(
    word: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isAlpha: Boolean = true,
    colors: ChipColors = suggestionChipColors(isAlpha = isAlpha)
) {
    SuggestionChip(
        onClick = onClick, modifier = modifier.then(
            if (onLongClick != null) Modifier.combinedClickable(
                onClick = onClick, onLongClick = onLongClick
            )
            else Modifier
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
        }, colors = colors, border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = enabled, borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    )
}

@Composable
fun suggestionChipColors(
    isAlpha: Boolean = false,

    containerColor: Color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
    labelColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,

    containerColorAlpha: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
    labelColorAlpha: Color = MaterialTheme.colorScheme.onPrimaryContainer
) = SuggestionChipDefaults.suggestionChipColors(
    containerColor = when {
        isAlpha -> containerColorAlpha
        else -> containerColor
    },
    labelColor = when {
        isAlpha -> labelColorAlpha
        else -> labelColor
    }
)

@Preview
@Composable
private fun WordChipPreview() {
    LingoLensTheme() {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)) {
                WordChip(word = "Example", onClick = {}, isAlpha = true)
                WordChip(word = "Example", onClick = {}, isAlpha = false)
            }
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
                WordChip(word = "Example", onClick = {}, isAlpha = true)
                WordChip(word = "Example", onClick = {}, isAlpha = false)
            }
        }
    }
}