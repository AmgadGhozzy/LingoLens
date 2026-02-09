package com.venom.ui.components.other

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.venom.ui.components.common.adp

@Preview
@Composable
fun TextShimmer(
    modifier: Modifier = Modifier,
    lines: Int = 2,
    lineWidths: List<Float> = listOf(0.9f, 0.8f),
    lineHeight: Int = 20,
    spacing: Int = 12
) {
    Column(
        modifier = modifier.padding(vertical = 12.adp, horizontal = 8.adp),
        verticalArrangement = Arrangement.spacedBy(spacing.adp)
    ) {
        repeat(lines) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(lineWidths.getOrElse(index) { 0.75f })
                    .height(lineHeight.adp)
                    .clip(RoundedCornerShape((lineHeight * 0.3).adp))
                    .animatedShimmer()
            )
        }
    }
}

fun Modifier.animatedShimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val offsetAnimation = transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    ).value

    val colors = listOf(
        MaterialTheme.colorScheme.surface.copy(0.3f),
        MaterialTheme.colorScheme.primary.copy(0.5f),
        MaterialTheme.colorScheme.secondary.copy(0.3f),
        MaterialTheme.colorScheme.surface.copy(0.1f)
    )

    background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(offsetAnimation - 400f, offsetAnimation - 200f),
            end = Offset(offsetAnimation + 400f, offsetAnimation + 200f)
        )
    )
}

fun Modifier.animatedShimmerWave(): Modifier = composed {
    val transition = rememberInfiniteTransition()

    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    background(
        MaterialTheme.colorScheme.surface.copy(alpha)
    )
}
