package com.venom.stackcard.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.venom.stackcard.ui.viewmodel.QuizOption
import com.venom.ui.components.common.DynamicStyledText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionItem(
    option: QuizOption,
    isAnswered: Boolean,
    onClick: () -> Unit,
    onSpeakClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Simplified animation states
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            option.isSelected -> 1f
            else -> 0.98f
        },
        animationSpec = spring(stiffness = 300f, dampingRatio = 0.6f),
        label = "scale"
    )

    // Pulsing animation only for correct answers
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Determine component states
    val isCorrectAnswer = isAnswered && option.isCorrect
    val isIncorrectSelection = isAnswered && option.isSelected && !option.isCorrect

    // Animated colors for smoother transitions
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isCorrectAnswer -> MaterialTheme.colorScheme.primaryContainer
            isIncorrectSelection -> MaterialTheme.colorScheme.errorContainer
            option.isSelected -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        },
        label = "background"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isCorrectAnswer -> MaterialTheme.colorScheme.onPrimaryContainer
            isIncorrectSelection -> MaterialTheme.colorScheme.onErrorContainer
            option.isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "content"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isCorrectAnswer -> MaterialTheme.colorScheme.primary
            isIncorrectSelection -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.primary
        },
        label = "border"
    )

    // Dynamic elevation for visual hierarchy
    val elevation = when {
        isCorrectAnswer -> 8.dp + (pulseGlow.dp * 0.5f)
        option.isSelected -> 6.dp
        else -> 2.dp
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = elevation,
                shape = ShapeDefaults.Large,
                ambientColor = if (isCorrectAnswer) MaterialTheme.colorScheme.primary else Color.Transparent,
                spotColor = if (isCorrectAnswer) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .semantics {
                contentDescription = "Option ${option.label}: ${option.text}"
                role = Role.RadioButton
                onClick(label = "Select option") { onClick(); true }
            },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = BorderStroke(width = 2.dp, color = borderColor),
        shape = ShapeDefaults.Large,
        onClick = if (!isAnswered) onClick else ({}),
        enabled = !isAnswered,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option label/indicator
            OptionIndicator(
                option = option,
                isAnswered = isAnswered,
                contentColor = contentColor
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Option text content
            DynamicStyledText(
                text = option.text,
                textAlign = TextAlign.Start,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            // Optional speak button
            onSpeakClick?.let {
                IconButton(
                    onClick = onSpeakClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Read option ${option.label} aloud",
                        tint = contentColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionIndicator(
    option: QuizOption,
    isAnswered: Boolean,
    contentColor: Color
) {
    // Simplified background color determination
    val backgroundColor = if (option.isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    // Determine icon based on state
    val icon: ImageVector? = when {
        isAnswered && option.isCorrect -> Icons.Default.Check
        isAnswered && option.isSelected -> Icons.Default.Close
        else -> null
    }

    // Animation for correct icon
    val iconScale by animateFloatAsState(
        targetValue = if (isAnswered && option.isCorrect) 1.2f else 1f,
        label = "icon scale"
    )

    Surface(
        modifier = Modifier
            .size(40.dp)
            .clip(ShapeDefaults.Small),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = if (option.isCorrect) "Correct" else "Incorrect",
                    tint = contentColor,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(iconScale)
                )
            } else {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
            }
        }
    }
}