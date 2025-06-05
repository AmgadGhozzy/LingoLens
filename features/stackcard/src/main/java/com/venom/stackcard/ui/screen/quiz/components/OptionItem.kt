package com.venom.stackcard.ui.screen.quiz.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun OptionItem(
    option: String,
    label: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !isAnswered && isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            isAnswered && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.2f)
            isAnswered && isSelected && !isCorrect -> Color(0xFFF44336).copy(alpha = 0.2f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300)
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !isAnswered && isSelected -> MaterialTheme.colorScheme.primary
            isAnswered && isCorrect -> Color(0xFF4CAF50)
            isAnswered && isSelected && !isCorrect -> Color(0xFFF44336)
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        },
        animationSpec = tween(300)
    )

    val scale by animateFloatAsState(
        targetValue = if (!isAnswered && isSelected) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.8f)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .animateContentSize(),
        onClick = if (!isAnswered) onClick else ({}),
        enabled = !isAnswered,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, borderColor),
        interactionSource = interactionSource,
        shadowElevation = if (isSelected && !isAnswered) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OptionLabel(
                label = label,
                isCorrect = isCorrect,
                isSelected = isSelected,
                isAnswered = isAnswered
            )

            Text(
                text = option,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OptionLabel(
    label: String,
    isCorrect: Boolean,
    isSelected: Boolean,
    isAnswered: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isAnswered && isCorrect -> Color(0xFF4CAF50)
            isAnswered && isSelected -> Color(0xFFF44336)
            else -> MaterialTheme.colorScheme.primaryContainer
        },
        animationSpec = tween(300)
    )

    val contentColor = when {
        isAnswered && (isCorrect || isSelected) -> Color.White
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Surface(
        modifier = Modifier.size(44.dp),
        color = backgroundColor,
        contentColor = contentColor,
        shape = CircleShape,
        shadowElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedContent(
                targetState = if (isAnswered) {
                    if (isCorrect) "✓" else if (isSelected) "✗" else label
                } else label,
                transitionSpec = {
                    scaleIn() + fadeIn() with scaleOut() + fadeOut()
                }
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
