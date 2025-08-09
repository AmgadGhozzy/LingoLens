package com.venom.stackcard.ui.screen.quiz.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.domain.model.WordLevels
import com.venom.resources.R
import com.venom.ui.components.other.GlassCard

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

    val scale by animateFloatAsState(
        targetValue = if (expanded) 1.03f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )

    val contentColor = MaterialTheme.colorScheme.onSurface
    val secondaryContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        onClick = { if (isUnlocked) expanded = !expanded },
        solidBackground = getGlassBackground(level, isUnlocked),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
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
                    LevelIcon(level = level, isUnlocked = isUnlocked)
                    Spacer(modifier = Modifier.width(16.dp))
                    LevelInfo(
                        level = level,
                        contentColor = contentColor,
                        secondaryContentColor = secondaryContentColor
                    )
                }

                if (isUnlocked) {
                    ProgressIndicator(progress, level, contentColor)
                } else {
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = stringResource(R.string.locked),
                                tint = secondaryContentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(level.descRes),
                style = MaterialTheme.typography.bodyMedium,
                color = secondaryContentColor,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal
            )

            AnimatedVisibility(
                visible = expanded && isUnlocked,
                enter = fadeIn(tween(400)) + expandVertically(tween(400)),
                exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    LevelActions(
                        onLearnClick = onLearnClick,
                        onTestClick = onTestClick,
                        level = level,
                        borderColor = borderColor
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelIcon(
    level: WordLevels,
    isUnlocked: Boolean
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                color = if (isUnlocked)
                    level.color.copy(alpha = 0.9f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(level.iconRes),
                contentDescription = null,
                tint = if (isUnlocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(30.dp)
            )
        }

        if (isUnlocked) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(20.dp),
                shape = RoundedCornerShape(18.dp),
                color = level.color.copy(alpha = 0.3f)
            ) {}
        }
    }
}

@Composable
private fun LevelInfo(
    level: WordLevels,
    contentColor: Color,
    secondaryContentColor: Color
) {
    Column {
        Text(
            text = stringResource(level.titleRes),
            style = MaterialTheme.typography.headlineSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${level.range.end - level.range.start + 1} ${stringResource(R.string.words)}",
            style = MaterialTheme.typography.bodyMedium,
            color = secondaryContentColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LevelActions(
    onLearnClick: () -> Unit,
    onTestClick: () -> Unit,
    level: WordLevels,
    borderColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onLearnClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = level.color,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 6.dp
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.learn),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = onTestClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, borderColor),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Quiz,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.test),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProgressIndicator(
    progress: Float,
    level: WordLevels,
    contentColor: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000)
    )

    Box(
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(60.dp),
            strokeWidth = 5.dp,
            strokeCap = StrokeCap.Round,
            color = level.color,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun getGlassBackground(
    level: WordLevels,
    isUnlocked: Boolean
): Color {
    return when {
        !isUnlocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        else -> level.color.copy(alpha = 0.15f)
    }
}