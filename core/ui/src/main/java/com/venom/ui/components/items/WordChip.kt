package com.venom.ui.components.items

import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign

@Composable
fun WordChip(
    word: String,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    isAlpha: Boolean = false,
    colors: ChipColors = SuggestionChipColors(isSelected = selected, isAlpha = isAlpha),
    modifier: Modifier = Modifier
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
fun SuggestionChipColors(
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
    labelColor: Color = MaterialTheme.colorScheme.onSurface,

    containerColorAlpha: Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
    labelColorAlpha: Color = MaterialTheme.colorScheme.onPrimaryContainer,

    selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
    selectedLabelColor: Color = MaterialTheme.colorScheme.onPrimary,

    disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    disabledLabelColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    isSelected: Boolean = false,
    isAlpha: Boolean = false
) = SuggestionChipDefaults.suggestionChipColors(
    containerColor = if (isSelected) selectedContainerColor else if (isAlpha) containerColorAlpha else containerColor,
    labelColor = if (isSelected) selectedLabelColor else if (isAlpha) labelColorAlpha else labelColor,
    disabledContainerColor = disabledContainerColor,
    disabledLabelColor = disabledLabelColor
)

