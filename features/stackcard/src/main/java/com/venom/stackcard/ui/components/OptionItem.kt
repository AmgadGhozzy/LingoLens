package com.venom.stackcard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.venom.stackcard.ui.viewmodel.QuizOption
import com.venom.ui.components.common.DynamicStyledText

@Composable
fun OptionItem(
    option: QuizOption,
    isAnswered: Boolean,
    onClick: () -> Unit,
    onSpeakClick: (() -> Unit)? = null
) {
    // Determine colors based on selection and answer state
    val colors = getOptionColors(option, isAnswered)

    // Animation for selection feedback
    val scale by animateFloatAsState(
        targetValue = if (option.isSelected) 1f else 0.96f,
        label = "scale animation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isAnswered, onClick = onClick)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = colors.background,
            contentColor = colors.content
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (option.isSelected) 12.dp else 8.dp
        ),
        shape = ShapeDefaults.Large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OptionLabel(
                label = if (isAnswered) {
                    if (option.isCorrect) "✓" else "✗"
                } else {
                    option.label
                },
                isSelected = option.isSelected,
                contentColor = colors.content
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Option content (main text)
            DynamicStyledText(
                text = option.text,
                textAlign = TextAlign.Start,
                color = colors.content,
                modifier = Modifier.weight(1f)
            )

            // Speak button (if provided)
            onSpeakClick?.let {
                IconButton(
                    onClick = onSpeakClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Read option ${option.label} aloud",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionLabel(
    label: String,
    isSelected: Boolean,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .clip(ShapeDefaults.Small),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = contentColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private data class OptionColors(
    val background: androidx.compose.ui.graphics.Color,
    val content: androidx.compose.ui.graphics.Color
)

@Composable
private fun getOptionColors(option: QuizOption, isAnswered: Boolean): OptionColors {
    val backgroundColor = when {
        isAnswered && option.isCorrect -> MaterialTheme.colorScheme.primaryContainer
        isAnswered && option.isSelected -> MaterialTheme.colorScheme.errorContainer
        option.isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when {
        option.isSelected && isAnswered && option.isCorrect -> MaterialTheme.colorScheme.onPrimaryContainer
        option.isSelected && isAnswered && !option.isCorrect -> MaterialTheme.colorScheme.onErrorContainer
        option.isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    return OptionColors(backgroundColor, contentColor)
}