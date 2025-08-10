package com.venom.ui.components.items

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.ThemeColors.GlassPrimary
import com.venom.ui.theme.ThemeColors.GlassSecondary
import com.venom.ui.theme.ThemeColors.GlassTertiary

@Composable
fun WordChip(
    word: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    isActive: Boolean = false
) {
    val radiatorBrush = if (isActive) {
        Brush.linearGradient(
            colors = listOf(
                GlassPrimary.copy(0.25f),
                GlassSecondary.copy(0.15f),
                GlassTertiary.copy(0.1f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                GlassPrimary.copy(0.15f),
                GlassSecondary.copy(0.12f),
                GlassTertiary.copy(0.09f)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background.copy(0.1f))
            .background(radiatorBrush)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                onClick = onClick,
                onClickLabel = word
            )
            .let { mod ->
                onLongClick?.let { longClick ->
                    mod.clickable(onClickLabel = "Long click $word") { longClick() }
                } ?: mod
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(0.9f)
            }
        )
    }
}

@Preview
@Composable
private fun WordChipPreview() {
    LingoLensTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Modern Radiator Liquid Glass WordChips",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                WordChip(
                    word = "Active",
                    onClick = {},
                    isActive = true
                )
                WordChip(
                    word = "Normal",
                    onClick = {},
                    isActive = false
                )
                WordChip(
                    word = "Example",
                    onClick = {},
                    onLongClick = {},
                    isActive = false
                )
            }

            // Additional row for more examples
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                WordChip(
                    word = "Radiator",
                    onClick = {},
                    isActive = true
                )
                WordChip(
                    word = "Liquid",
                    onClick = {},
                    isActive = false
                )
                WordChip(
                    word = "Glass",
                    onClick = {},
                    isActive = false
                )
            }
        }
    }
}