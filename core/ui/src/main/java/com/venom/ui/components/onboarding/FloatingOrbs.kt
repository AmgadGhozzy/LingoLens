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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.ui.theme.ThemeColors.Indigo
import com.venom.ui.theme.ThemeColors.Purple

data class OrbConfig(
    val size: Dp,
    val color: Color,
    val alignment: Alignment,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp,
    val scaleMultiplier: Float = 1f,
    val alphaMultiplier: Float = 1f
)

@Composable
fun FloatingOrbs(
    modifier: Modifier = Modifier,
    primaryColor: Color = Indigo,
    secondaryColor: Color = Purple,
    customOrbs: List<OrbConfig>? = null,
    animationDuration: Int = 6000,
    enableFloatingAnimation: Boolean = true,
    enableScaleAnimation: Boolean = true,
    enableAlphaAnimation: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating_orbs_transition")

    // Scale animations
    val scale1 by infiniteTransition.animateFloat(
        initialValue = if (enableScaleAnimation) 0.8f else 1f,
        targetValue = if (enableScaleAnimation) 1.3f else 1f,
        animationSpec = infiniteRepeatable(
            tween(animationDuration, easing = EaseInOutCubic),
            RepeatMode.Reverse
        )
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = if (enableScaleAnimation) 0.6f else 1f,
        targetValue = if (enableScaleAnimation) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            tween((animationDuration * 1.33f).toInt(), easing = EaseInOutCubic),
            RepeatMode.Reverse
        )
    )

    val scale3 by infiniteTransition.animateFloat(
        initialValue = if (enableScaleAnimation) 0.9f else 1f,
        targetValue = if (enableScaleAnimation) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            tween((animationDuration * 1.17f).toInt(), easing = EaseInOutCubic),
            RepeatMode.Reverse
        )
    )

    // Alpha animation
    val alpha by infiniteTransition.animateFloat(
        initialValue = if (enableAlphaAnimation) 0.2f else 0.4f,
        targetValue = if (enableAlphaAnimation) 0.6f else 0.4f,
        animationSpec = infiniteRepeatable(
            tween((animationDuration * 0.83f).toInt(), easing = EaseInOutCubic),
            RepeatMode.Reverse
        )
    )

    // Floating animation
    val offsetY by infiniteTransition.animateFloat(
        initialValue = if (enableFloatingAnimation) -20f else 0f,
        targetValue = if (enableFloatingAnimation) 20f else 0f,
        animationSpec = infiniteRepeatable(
            tween((animationDuration * 0.67f).toInt(), easing = EaseInOutCubic),
            RepeatMode.Reverse
        )
    )

    val orbs = customOrbs ?: getDefaultOrbs(primaryColor, secondaryColor)

    Box(modifier = modifier.fillMaxSize()) {
        orbs.forEachIndexed { index, orb ->
            val scaleValue = when (index % 3) {
                0 -> scale1 * orb.scaleMultiplier
                1 -> scale2 * orb.scaleMultiplier
                else -> scale3 * orb.scaleMultiplier
            }

            val dynamicOffsetY = when (index % 3) {
                0 -> offsetY
                1 -> -offsetY
                else -> offsetY * 0.5f
            }

            Box(
                modifier = Modifier
                    .align(orb.alignment)
                    .offset(
                        x = orb.offsetX,
                        y = orb.offsetY + dynamicOffsetY.dp
                    )
                    .size(orb.size)
                    .scale(scaleValue)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                orb.color.copy(alpha = alpha * orb.alphaMultiplier),
                                orb.color.copy(alpha = alpha * orb.alphaMultiplier * 0.3f),
                                Color.Transparent
                            ),
                            radius = orb.size.value * 1.5f
                        ),
                        CircleShape
                    )
            )
        }
    }
}

private fun getDefaultOrbs(primaryColor: Color, secondaryColor: Color): List<OrbConfig> {
    return listOf(
        OrbConfig(
            size = 320.dp,
            color = primaryColor,
            alignment = Alignment.TopStart,
            offsetX = (-100).dp,
            offsetY = (-80).dp,
            scaleMultiplier = 1f,
            alphaMultiplier = 0.8f
        ),
        OrbConfig(
            size = 280.dp,
            color = secondaryColor,
            alignment = Alignment.CenterEnd,
            offsetX = 60.dp,
            offsetY = 0.dp,
            scaleMultiplier = 1f,
            alphaMultiplier = 0.6f
        ),
        OrbConfig(
            size = 240.dp,
            color = primaryColor,
            alignment = Alignment.BottomStart,
            offsetX = (-60).dp,
            offsetY = 100.dp,
            scaleMultiplier = 1f,
            alphaMultiplier = 0.4f
        ),
        OrbConfig(
            size = 160.dp,
            color = primaryColor,
            alignment = Alignment.TopEnd,
            offsetX = 40.dp,
            offsetY = 120.dp,
            scaleMultiplier = 0.6f,
            alphaMultiplier = 0.3f
        )
    )
}
