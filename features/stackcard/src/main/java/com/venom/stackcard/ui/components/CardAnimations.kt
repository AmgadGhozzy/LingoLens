package com.venom.stackcard.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object EnhancedCardAnimations {
    const val SWIPE_THRESHOLD = 300f
    const val MAX_ROTATION_DEGREES = 15f

    val SwipeAnimationSpec = tween<Float>(
        durationMillis = 300,
        easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
    )

    val ReturnAnimationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow,
        visibilityThreshold = 0.01f
    )

    val FlipAnimationSpec = keyframes {
        durationMillis = 600
        0f at 0 using FastOutSlowInEasing
        90f at 200 using LinearEasing
        180f at 400 using FastOutSlowInEasing
        360f at 600 using FastOutSlowInEasing
    }

    val ScaleAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = 300f
    )

    // Constants for animation limits
    fun calculateRotation(offsetX: Float, velocity: Float = 0f): Float {
        val baseRotation = (offsetX * 0.04f).coerceIn(-MAX_ROTATION_DEGREES, MAX_ROTATION_DEGREES)
        val velocityInfluence = (velocity * 0.001f).coerceIn(-5f, 5f)
        return baseRotation + velocityInfluence
    }
}


@Composable
fun rememberCardAnimationState(): CardAnimationState {
    return remember {
        CardAnimationState(
            offsetX = Animatable(0f),
            offsetY = Animatable(0f),
            rotation = Animatable(0f),
            scale = Animatable(1f),
            rotationY = Animatable(0f),
            alpha = Animatable(1f),
            blur = Animatable(0f)
        )
    }
}

suspend fun resetAnimationState(animState: CardAnimationState) {
    animState.apply {
        coroutineScope {
            launch { offsetX.snapTo(0f) }
            launch { offsetY.snapTo(0f) }
            launch { rotation.snapTo(0f) }
            launch { scale.snapTo(1f) }
            launch { rotationY.snapTo(0f) }
            launch { alpha.snapTo(1f) }
            launch { blur.snapTo(0f) }
        }
    }
}

suspend fun returnToCenter(animState: CardAnimationState) {
    animState.apply {
        coroutineScope {
            launch {
                offsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = EnhancedCardAnimations.ReturnAnimationSpec
                )
            }
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = EnhancedCardAnimations.ReturnAnimationSpec
                )
            }
            launch {
                rotation.animateTo(
                    targetValue = 0f,
                    animationSpec = EnhancedCardAnimations.ReturnAnimationSpec
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = EnhancedCardAnimations.ScaleAnimationSpec
                )
            }
            launch {
                blur.animateTo(
                    targetValue = 0f,
                    animationSpec = EnhancedCardAnimations.ReturnAnimationSpec
                )
            }
        }
    }
}

data class CardAnimationState(
    val offsetX: Animatable<Float, AnimationVector1D>,
    val offsetY: Animatable<Float, AnimationVector1D>,
    val rotation: Animatable<Float, AnimationVector1D>,
    val scale: Animatable<Float, AnimationVector1D>,
    val rotationY: Animatable<Float, AnimationVector1D>,
    val alpha: Animatable<Float, AnimationVector1D>,
    val blur: Animatable<Float, AnimationVector1D>
)
