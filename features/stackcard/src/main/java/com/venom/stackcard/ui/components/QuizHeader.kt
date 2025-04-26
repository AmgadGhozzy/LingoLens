package com.venom.stackcard.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.resources.R
import com.venom.ui.components.other.ProgressBar
import com.venom.ui.components.other.TimerDisplay

@Composable
fun FloatingHeart(
    modifier: Modifier = Modifier,
    earnedHeart: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HeartAnimation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (earnedHeart) 1.5f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartScale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartAlpha"
    )

    Icon(
        imageVector = Icons.Rounded.Favorite,
        contentDescription = null,
        tint = if (earnedHeart) Color.Green else Color.Red,
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
    )
}

@Composable
fun QuizHeader(
    currentQuestion: Int,
    totalQuestions: Int,
    hearts: Int,
    timeRemaining: Int,
    streak: Int = 0,
    score: Float = 0f,
    showHeartAnimation: Boolean = false,
    earnedHeart: Boolean = false,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top row with hearts, progress and timer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hearts indicator
            Box(contentAlignment = Alignment.Center) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = stringResource(R.string.hearts),
                        tint = if (showHeartAnimation) Color.Red else Color.Red.copy(alpha = 0.9f),
                        modifier = Modifier
                            .size(32.dp)
                            .scale(if (showHeartAnimation) 1.2f else 1f)
                    )
                    Text(
                        text = hearts.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = showHeartAnimation,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FloatingHeart(earnedHeart = earnedHeart)
                }
            }

            ProgressBar(
                progress = currentQuestion.toFloat() / totalQuestions,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "$currentQuestion/$totalQuestions",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            TimerDisplay(timeRemaining = timeRemaining)
        }

        // Bottom row with score and streak
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Score display
            Text(
                text = "Score: ${score.toInt()}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Streak display (only show if > 0)
            if (streak > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color = when {
                                streak >= 5 -> Color(0xFFFF5722) // Orange for high streak
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = when {
                            streak >= 5 -> Color.White
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = "$streak",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = when {
                            streak >= 5 -> Color.White
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
    }
}