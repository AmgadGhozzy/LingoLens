package com.venom.stackcard.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.domain.model.WordLevels
import com.venom.resources.R

@Composable
fun LevelCard(
    level: WordLevels,
    isUnlocked: Boolean,
    progress: Float = 0f,
    onTestClick: () -> Unit,
    onLearnClick: () -> Unit,
    isCurrentLevel: Boolean = false
) {
    var expanded by remember { mutableStateOf(isCurrentLevel) }
    val isCompleted = progress >= 0.9f

    val cardColors = getCardColors(isUnlocked, isCompleted)

    val elevation by animateDpAsState(
        targetValue = if (expanded) 2.dp else 16.dp,
        animationSpec = spring(dampingRatio = 0.6f)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFF6366F1).copy(alpha = 0.15f),
                spotColor = Color(0xFF6366F1).copy(alpha = 0.25f)
            )
            .pointerInput(Unit) {
                detectTapGestures { expanded = !expanded }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = cardColors.background)
                .padding(24.dp)
        ) {
            LevelHeader(
                level = level,
                isUnlocked = isUnlocked,
                progress = progress,
                isCompleted = isCompleted,
                colors = cardColors
            )
            Spacer(modifier = Modifier.height(20.dp))
            LevelDescription(level = level)
            AnimatedVisibility(
                visible = expanded && isUnlocked,
                enter = fadeIn(tween(400)) + expandVertically() + scaleIn(tween(400)),
                exit = fadeOut(tween(200)) + shrinkVertically() + scaleOut(tween(200))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    LevelActions(
                        onLearnClick = onLearnClick,
                        onTestClick = onTestClick,
                        isCompleted = isCompleted
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelHeader(
    level: WordLevels,
    isUnlocked: Boolean,
    progress: Float,
    isCompleted: Boolean,
    colors: CardColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            LevelIcon(level = level, colors = colors)
            Spacer(modifier = Modifier.width(20.dp))
            LevelInfo(level = level, colors = colors)
        }

        LevelStatus(
            isUnlocked = isUnlocked,
            progress = progress,
            isCompleted = isCompleted
        )
    }
}

@Composable
private fun LevelIcon(
    level: WordLevels,
    colors: CardColors
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(colors.iconBackground),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(level.iconRes),
            contentDescription = null,
            tint = colors.iconTint,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun LevelInfo(
    level: WordLevels,
    colors: CardColors
) {
    Column {
        Text(
            text = stringResource(level.titleRes),
            style = MaterialTheme.typography.titleMedium,
            color = colors.titleColor,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${level.range.end - level.range.start + 1} ${stringResource(R.string.words)}",
            style = MaterialTheme.typography.bodySmall,
            color = colors.subtitleColor
        )
    }
}

@Composable
private fun LevelStatus(
    isUnlocked: Boolean,
    progress: Float,
    isCompleted: Boolean
) {
    when {
        !isUnlocked -> {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        isCompleted -> {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        progress > 0f -> {
            ProgressIndicator(progress = progress)
        }
    }
}

@Composable
private fun ProgressIndicator(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(48.dp)
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(44.dp),
            strokeWidth = 4.dp,
            strokeCap = StrokeCap.Round,
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        )

        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LevelDescription(level: WordLevels) {
    Text(
        text = stringResource(level.descRes),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 20.sp
    )
}

@Composable
private fun LevelActions(
    onLearnClick: () -> Unit,
    onTestClick: () -> Unit,
    isCompleted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onLearnClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.learn),
                fontSize = 14.sp
            )
        }

        Button(
            onClick = onTestClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCompleted)
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.Quiz,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.test),
                fontSize = 14.sp
            )
        }
    }
}

// Data class for consistent color theming
private data class CardColors(
    val background: Color,
    val titleColor: Color,
    val subtitleColor: Color,
    val iconBackground: Color,
    val iconTint: Color
)

@Composable
private fun getCardColors(isUnlocked: Boolean, isCompleted: Boolean): CardColors {
    return when {
        !isUnlocked -> CardColors(
            background = MaterialTheme.colorScheme.surfaceVariant,
            titleColor = MaterialTheme.colorScheme.onSurfaceVariant,
            subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            iconBackground = MaterialTheme.colorScheme.surface,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        isCompleted -> CardColors(
            background = MaterialTheme.colorScheme.primaryContainer,
            titleColor = MaterialTheme.colorScheme.onPrimaryContainer,
            subtitleColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            iconBackground = MaterialTheme.colorScheme.primary,
            iconTint = MaterialTheme.colorScheme.onPrimary
        )

        else -> CardColors(
            background = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
            titleColor = MaterialTheme.colorScheme.onPrimaryContainer,
            subtitleColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            iconBackground = MaterialTheme.colorScheme.primaryContainer,
            iconTint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLevelCard() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Locked level
            LevelCard(
                level = WordLevels.values().first(),
                isUnlocked = false,
                progress = 0f,
                onLearnClick = {},
                onTestClick = {}
            )

            // In progress level
            LevelCard(
                level = WordLevels.values()[1],
                isUnlocked = true,
                progress = .5f,
                onLearnClick = {},
                onTestClick = {},
                isCurrentLevel = true
            )

            // Completed level
            LevelCard(
                level = WordLevels.values().last(),
                isUnlocked = true,
                progress = 1f,
                onLearnClick = {},
                onTestClick = {}
            )
        }
    }
}