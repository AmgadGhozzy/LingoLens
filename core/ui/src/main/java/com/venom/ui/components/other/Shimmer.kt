package com.venom.ui.components.other

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun rememberShimmerBrush(
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
    ),
    animationDuration: Int = 2000
): Brush {
    val transition = rememberInfiniteTransition()
    val offset = transition.animateFloat(
        initialValue = -600f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    return remember(offset.value) {
        Brush.linearGradient(
            colors = colors,
            start = Offset(offset.value - 300f, 0f),
            end = Offset(offset.value + 300f, 0f)
        )
    }
}