package com.venom.ui.components.other

import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun ProgressBar(
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 24.dp,
    cornerRadius: Dp = 12.dp,
    animationDuration: Int = 500,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    ),
    contentPadding: Dp = 2.dp
) {

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(animationDuration),
        label = "progress"
    )

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(contentPadding)
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.horizontalGradient(colors = gradientColors)
                )
        )
    }
}
