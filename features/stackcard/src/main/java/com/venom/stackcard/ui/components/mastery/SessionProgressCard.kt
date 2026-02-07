package com.venom.stackcard.ui.components.mastery

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.venom.domain.model.AppTheme
import com.venom.domain.model.WordProgressState
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.theme.BrandColors
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.lingoLens

@Composable
fun SessionProgressCard(
    state: WordProgressState,
    modifier: Modifier = Modifier
) {
    val semantic = MaterialTheme.lingoLens.semantic

    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 20,
        contentPadding = 16
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.adp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.adp)) {
                    Text(
                        text = "Session Complete",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = buildString {
                            append("${state.completed} words reviewed")
                            if (state.sessionDurationMs > 0) {
                                val mins = (state.sessionDurationMs / 60000).toInt()
                                val secs = ((state.sessionDurationMs % 60000) / 1000).toInt()
                                if (mins > 0) append(" · ${mins}m ${secs}s")
                                else append(" · ${secs}s")
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.adp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    XpBadge(xp = state.xpEarned)
                    StreakIndicator(streak = state.currentStreak)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = R.drawable.ic_seal_check,
                    value = state.mastered.toString(),
                    label = "Mastered",
                    color = semantic.success
                )
                StatItem(
                    icon = R.drawable.ic_book_open,
                    value = state.learning.toString(),
                    label = "Learning",
                    color = semantic.warning
                )
                StatItem(
                    icon = R.drawable.ic_retry,
                    value = state.needsReview.toString(),
                    label = "Review",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (state.xpToNextLevel > 0) {
                XpProgressBar(current = state.xpEarned, max = state.xpToNextLevel)
            }

            if (state.totalSessionCount > 0) {
                Text(
                    text = "Session #${state.totalSessionCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun XpBadge(
    xp: Int,
    modifier: Modifier = Modifier
) {
    val xpColor = BrandColors.Purple400

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.adp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        BrandColors.Purple500.copy(alpha = 0.2f),
                        BrandColors.Indigo500.copy(alpha = 0.15f)
                    )
                )
            )
            .border(1.adp, BrandColors.Purple400.copy(alpha = 0.3f), RoundedCornerShape(10.adp))
            .padding(horizontal = 10.adp, vertical = 6.adp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.adp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_lightning),
            contentDescription = null,
            modifier = Modifier.size(14.adp),
            tint = xpColor
        )
        Text(
            text = "+$xp",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                fontSize = 12.asp
            ),
            color = xpColor
        )
        Text(
            text = "XP",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 9.asp
            ),
            color = xpColor.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun XpProgressBar(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = (current.toFloat() / max).coerceIn(0f, 1f),
        animationSpec = tween(800, easing = FastOutSlowInEasing)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.adp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Level Progress",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$current / $max XP",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = BrandColors.Purple400
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.adp)
                .clip(RoundedCornerShape(3.adp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.adp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(BrandColors.Purple500, BrandColors.Indigo500)
                        )
                    )
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: Int,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.adp)
    ) {
        Box(
            modifier = Modifier
                .size(40.adp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.adp),
                tint = color
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StreakIndicator(
    streak: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flame")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            tween(500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "scale"
    )
    val flameRotation by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(300), RepeatMode.Reverse),
        label = "rotate"
    )

    val flameColor = when {
        streak >= 30 -> BrandColors.Red500
        streak >= 14 -> BrandColors.Amber500
        streak >= 7 -> BrandColors.Amber400
        else -> BrandColors.Yellow500
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.adp))
            .background(flameColor.copy(alpha = 0.12f))
            .border(1.adp, flameColor.copy(alpha = 0.25f), RoundedCornerShape(10.adp))
            .padding(horizontal = 10.adp, vertical = 6.adp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.adp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_flame),
            contentDescription = null,
            modifier = Modifier
                .size(16.adp)
                .scale(flameScale)
                .rotate(flameRotation),
            tint = flameColor
        )
        Text(
            text = "$streak",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
            color = flameColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun SessionProgressCardPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        SessionProgressCard(
            state = WordProgressState(
                completed = 10,
                total = 10,
                mastered = 6,
                learning = 3,
                needsReview = 1,
                currentStreak = 7,
                xpEarned = 85,
                xpToNextLevel = 100,
                sessionDurationMs = 185000,
                totalSessionCount = 12
            ),
            modifier = Modifier.padding(16.adp)
        )
    }
}