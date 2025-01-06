package com.venom.wordcard.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object WordCardAnimations {
    const val SWIPE_THRESHOLD = 300f

    val SwipeAnimationSpec = tween<Float>(
        durationMillis = 300, easing = FastOutSlowInEasing
    )

    val ReturnAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
    )

    val FlipAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow
    )

    @androidx.compose.runtime.Composable
    fun rememberCardAnimationState() = androidx.compose.runtime.remember {
        CardAnimationState(
            offsetX = Animatable(0f),
            offsetY = Animatable(0f),
            rotation = Animatable(0f),
            scale = Animatable(1f),
            rotationY = Animatable(0f)
        )
    }
}

data class CardAnimationState(
    val offsetX: Animatable<Float, AnimationVector1D>,
    val offsetY: Animatable<Float, AnimationVector1D>,
    val rotation: Animatable<Float, AnimationVector1D>,
    val scale: Animatable<Float, AnimationVector1D>,
    val rotationY: Animatable<Float, AnimationVector1D>
)