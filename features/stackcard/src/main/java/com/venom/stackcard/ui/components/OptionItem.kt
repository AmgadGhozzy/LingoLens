package com.venom.stackcard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (option.isSelected) 12.dp else 8.dp
        ),
        shape = ShapeDefaults.Large
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Option label
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(ShapeDefaults.Small)
                    .align(Alignment.CenterStart),
                color = if (option.isSelected) {
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
                        text = if (isAnswered) {
                            if (option.isCorrect) "✔" else "✘"
                        } else {
                            option.label
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Option content
            DynamicStyledText(
                text = option.text,
                textAlign = TextAlign.Center,
                color = contentColor,
                modifier = Modifier.align(Alignment.Center)
            )

            // Speak button
            onSpeakClick?.let {
                IconButton(
                    onClick = onSpeakClick,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterEnd)
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
