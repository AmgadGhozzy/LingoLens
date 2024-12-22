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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A pulse animation composable that creates a pulsing circular loading indicator.
 *
 * @param modifier Modifier to be applied to the animation container
 * @param color The color of the pulsing border (defaults to primary color scheme)
 * @param size The size of the pulsing circle (defaults to 100.dp)
 * @param animationDuration Duration of a single pulse animation cycle in milliseconds
 */
@Composable
fun PulseAnimation(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Float = 100f,
    animationDuration: Int = 600
) {
    // Create an Animatable for smooth, controlled animation
    val pulseAnimation = remember { Animatable(0.6f) }

    // Launch the infinite repeating animation
    LaunchedEffect(Unit) {
        pulseAnimation.animateTo(
            targetValue = 1f, animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = animationDuration),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    // Render the pulsing box
    Box(modifier = modifier
        .size(size.dp)
        .graphicsLayer {
            // Reduce opacity as the animation progresses
            scaleX = pulseAnimation.value
            scaleY = pulseAnimation.value
            alpha = 1f - pulseAnimation.value
        }
        .border(
            width = 5.dp, color = color, shape = CircleShape
        ))
}

@Preview(showBackground = true)
@Composable
private fun PulseAnimationPreview() {
    PulseAnimation()
}