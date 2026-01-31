package com.venom.lingospell.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.venom.domain.model.AppTheme
import com.venom.lingospell.domain.MAX_STREAK
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.lingoLens

@Composable
fun StreakBar(
    streak: Int,
    isMastered: Boolean,
    modifier: Modifier = Modifier
) {
    val isComplete = streak >= MAX_STREAK

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.adp)
    ) {
        Text(
            text = "MASTERY",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.asp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.adp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(MAX_STREAK) { index ->
                val isActive = index < streak

                StreakBarSegment(
                    isActive = isActive,
                    isComplete = isComplete,
                    isMastered = isMastered,
                    index = index
                )
            }
        }
    }
}

@Composable
private fun StreakBarSegment(
    isActive: Boolean,
    isComplete: Boolean,
    isMastered: Boolean,
    index: Int
) {
    // Fill Color: mastery if complete+mastered, primary if complete, otherwise primary for active steps
    val fillColor = if (isComplete) {
        if (isMastered) MaterialTheme.lingoLens.feature.spelling.mastery else MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primary
    }
    val emptyColor = MaterialTheme.lingoLens.feature.spelling.streakEmpty

    val fillProgress by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "streakFill"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = index * 200 + 2000),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Box(
        modifier = Modifier
            .width(40.adp)
            .height(12.adp)
            .clip(RoundedCornerShape(6.adp))
            .background(emptyColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fillProgress)
                .background(fillColor)
        )

        if (isActive) {
            val offsetVal = (shimmerOffset * 40).adp
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .offset(x = offsetVal)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@Preview(name = "Light - Streak")
@Composable
private fun StreakBarLightPreview() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        StreakBar(
            streak = 2,
            isMastered = false
        )
    }
}
