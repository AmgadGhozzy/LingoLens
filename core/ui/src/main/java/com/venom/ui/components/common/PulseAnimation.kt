package com.venom.ui.components.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

/**
 * A pulse animation composable that creates multiple pulsing circular loading indicators.
 *
 * @param modifier Modifier to be applied to the animation container
 * @param color The color of the pulsing border (defaults to primary color scheme)
 * @param size The size of the largest pulsing circle (defaults to 100.dp)
 * @param animationDuration Duration of a single pulse animation cycle in milliseconds
 * @param circleCount Number of concentric circles to animate (defaults to 3)
 * @param delayBetweenCircles Delay between each circle's animation start in milliseconds
 */
@Composable
fun PulseAnimation(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Float = 100f,
    animationDuration: Int = 2500,
    circleCount: Int = 3,
    delayBetweenCircles: Int = 600
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Create multiple circles with staggered animations
        repeat(circleCount) { index ->
            PulseCircle(
                color = color,
                size = size,
                animationDuration = animationDuration,
                initialDelay = index * delayBetweenCircles
            )
        }
    }
}

@Composable
private fun PulseCircle(
    color: Color,
    size: Float,
    animationDuration: Int,
    initialDelay: Int
) {
    // Create an Animatable for smooth, controlled animation
    val pulseAnimation = remember { Animatable(0.3f) }

    // Launch the infinite repeating animation with initial delay
    LaunchedEffect(Unit) {
        delay(initialDelay.toLong())
        pulseAnimation.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = animationDuration),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    // Render the pulsing circle
    Box(
        modifier = Modifier
            .size(size.adp)
            .graphicsLayer {
                // Scale and fade as the animation progresses
                scaleX = pulseAnimation.value
                scaleY = pulseAnimation.value
                alpha = 1f - pulseAnimation.value
            }
            .border(
                width = 3.adp,
                color = color,
                shape = CircleShape
            )
    )
}

@Preview(showBackground = true)
@Composable
private fun PulseAnimationPreview() {
    PulseAnimation()
}