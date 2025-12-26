package com.venom.quiz.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onTestClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    isExpanded: Boolean = false,
    onExpandToggle: () -> Unit = {},
    compact: Boolean = false,
    modifier: Modifier = Modifier,
    animationDelay: Int = 0
) {
    // Shared animated scale for full card on expand
    var internalExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(isExpanded) {
        if (isExpanded && !internalExpanded) internalExpanded = true
        else if (!isExpanded && internalExpanded) internalExpanded = false
    }

    val scale by animateFloatAsState(
        targetValue = if (internalExpanded && !compact) 1.03f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )

    // Colors
    val contentColor = MaterialTheme.colorScheme.onSurface
    val secondaryContentColor = MaterialTheme.colorScheme.onSurface.copy(0.7f)
    val borderColor = MaterialTheme.colorScheme.outline.copy(0.3f)

    if (compact) {
        GlassCard(
            modifier = modifier.width(200.dp).height(280.dp),
            onClick = if (isUnlocked) onLearnClick else { {} },
            solidBackground = if (isUnlocked) level.color.copy(0.1f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = if (isUnlocked) level.color.copy(0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isUnlocked) {
                                Image(
                                    painter = painterResource(level.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(level.titleRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )

                    Text(
                        text = "${level.range.end - level.range.start + 1} words",
                        style = MaterialTheme.typography.bodySmall,
                        color = secondaryContentColor
                    )

                    if (isUnlocked) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${(progress * 100).toInt()}% Complete",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = level.color
                        )
                    }
                }

                if (isUnlocked) {
                    Button(
                        onClick = onLearnClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = level.color,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_cards2),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.learn), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    } else {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .scale(scale),
            onClick = {
                if (isUnlocked) {
                    internalExpanded = !internalExpanded
                    onExpandToggle()
                }
            },
            solidBackground = getGlassBackground(level, isUnlocked).copy(0.1f),
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
                        Image(
                            painter = painterResource(level.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(68.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        LevelInfo(
                            level = level,
                            contentColor = contentColor,
                            secondaryContentColor = secondaryContentColor
                        )
                    }

                    if (isUnlocked) {
                        ProgressIndicator(
                            progress = progress,
                            level = level,
                            contentColor = contentColor,
                            size = 60.dp,
                            textSize = 14.sp
                        )
                    } else {
                        Surface(
                            modifier = Modifier.size(60.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)
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
                    visible = internalExpanded && isUnlocked,
                    enter = fadeIn(tween(500)) + expandVertically(tween(500)),
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
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f),
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
    contentColor: Color,
    size: androidx.compose.ui.unit.Dp,
    textSize: androidx.compose.ui.unit.TextUnit
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
            modifier = Modifier.size(size),
            strokeWidth = if (size <= 60.dp) 5.dp else 6.dp,
            strokeCap = StrokeCap.Round,
            color = level.color,
            trackColor = MaterialTheme.colorScheme.outline.copy(0.2f)
        )
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontSize = textSize,
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
        !isUnlocked -> MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)
        else -> level.color.copy(0.15f)
    }
}
