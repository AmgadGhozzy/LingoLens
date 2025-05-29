package com.venom.ui.components.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FloatingOrbs(screen: OnboardingPage) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(6000, easing = EaseInOutCubic), RepeatMode.Reverse)
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = EaseInOutCubic), RepeatMode.Reverse)
    )

    val scale3 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(7000, easing = EaseInOutCubic), RepeatMode.Reverse)
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(5000, easing = EaseInOutCubic), RepeatMode.Reverse)
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutCubic), RepeatMode.Reverse)
    )

    Box(Modifier.fillMaxSize()) {
        listOf(
            Triple(320.dp, screen.primaryColor, scale1) to Modifier.align(Alignment.TopStart).offset((-100).dp, (-80).dp + offsetY.dp),
            Triple(280.dp, screen.secondaryColor, scale2) to Modifier.align(Alignment.CenterEnd).offset(60.dp, (-offsetY).dp),
            Triple(240.dp, screen.primaryColor, scale3) to Modifier.align(Alignment.BottomStart).offset((-60).dp, 100.dp + (offsetY * 0.5f).dp),
            Triple(160.dp, screen.primaryColor, scale1 * 0.6f) to Modifier.align(Alignment.TopEnd).offset(40.dp, 120.dp + (offsetY * 0.3f).dp)
        ).forEach { (sizeColorScale, baseModifier) ->
            val (size, color, scale) = sizeColorScale
            val alphaMultiplier = when (size) {
                280.dp -> 0.7f
                240.dp -> 0.5f
                160.dp -> 0.3f
                else -> 1f
            }

            Box(
                modifier = baseModifier
                    .size(size)
                    .scale(scale)
                    .background(
                        Brush.radialGradient(
                            listOf(color.copy(alpha = alpha * alphaMultiplier), Color.Transparent),
                            radius = 500f
                        ),
                        CircleShape
                    )
            )
        }
    }
}