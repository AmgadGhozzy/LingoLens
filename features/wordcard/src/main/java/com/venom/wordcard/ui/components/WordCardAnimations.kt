package com.venom.wordcard.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object WordCardAnimations {
    const val SWIPE_THRESHOLD = 200f

    val SwipeAnimationSpec = tween<Float>(
        durationMillis = 250,
        easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)  // Custom easing for natural movement
    )

    val ReturnAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
        visibilityThreshold = 0.01f
    )

    val FlipAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow,
        visibilityThreshold = 0.01f
    )

//    val FlipAnimationSpec = tween<Float>(
//        durationMillis = 400, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
//    )

    val ScaleAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 400f
    )

    // Constants for animation limits
    const val MAX_ROTATION_DEGREES = 12f

    // Helper function to calculate rotation based on offset
    fun calculateRotation(offsetX: Float): Float {
        return (offsetX * 0.03f).coerceIn(-MAX_ROTATION_DEGREES, MAX_ROTATION_DEGREES)
    }

    @Composable
    fun rememberCardAnimationState() = remember {
        CardAnimationState(
            offsetX = Animatable(0f),
            offsetY = Animatable(0f),
            rotation = Animatable(0f),
            scale = Animatable(1f),
            rotationY = Animatable(0f)
        )
    }
}

suspend fun resetAnimationState(cardAnimState: CardAnimationState) {
    cardAnimState.apply {
        offsetX.snapTo(0f)
        offsetY.snapTo(0f)
        rotation.snapTo(0f)
        scale.snapTo(1f)
        rotationY.snapTo(0f)
    }
}

suspend fun returnToCenter(cardAnimState: CardAnimationState) {
    cardAnimState.apply {
        coroutineScope {
            launch { offsetX.animateTo(0f, WordCardAnimations.ReturnAnimationSpec) }
            launch { offsetY.animateTo(0f, WordCardAnimations.ReturnAnimationSpec) }
            launch { rotation.animateTo(0f, WordCardAnimations.ReturnAnimationSpec) }
            launch { scale.animateTo(1f, WordCardAnimations.ScaleAnimationSpec) }
        }
    }
}

data class CardAnimationState(
    val offsetX: Animatable<Float, AnimationVector1D>,
    val offsetY: Animatable<Float, AnimationVector1D>,
    val rotation: Animatable<Float, AnimationVector1D>,
    val scale: Animatable<Float, AnimationVector1D>,
    val rotationY: Animatable<Float, AnimationVector1D>
)