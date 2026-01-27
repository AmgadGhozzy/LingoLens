package com.venom.stackcard.ui.components.flashcard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

object EnhancedCardAnimations {
    const val SWIPE_THRESHOLD_DP = 150
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

    val ScaleAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = 300f
    )

    private const val ROTATION_MULTIPLIER = 0.04f
    private const val VELOCITY_MULTIPLIER = 0.001f

    fun calculateRotation(offsetX: Float, velocity: Float = 0f): Float {
        val baseRotation =
            (offsetX * ROTATION_MULTIPLIER).coerceIn(-MAX_ROTATION_DEGREES, MAX_ROTATION_DEGREES)
        val velocityInfluence = (velocity * VELOCITY_MULTIPLIER).coerceIn(-5f, 5f)
        return baseRotation + velocityInfluence
    }
}

// Add DisposableEffect for proper cleanup
@Composable
fun rememberCardAnimationState(): CardAnimationState {
    val scope = rememberCoroutineScope()
    val state = remember {
        CardAnimationState(
            offsetX = Animatable(0f),
            offsetY = Animatable(0f),
            rotation = Animatable(0f),
            scale = Animatable(1f),
            rotationY = Animatable(0f),
            alpha = Animatable(1f),
            blur = Animatable(0f),
            scope = scope
        )
    }

    // Cleanup on disposal to prevent memory leaks
    DisposableEffect(Unit) {
        onDispose {
            state.cleanup()
        }
    }

    return state
}

// Use single coroutineScope to cancel all animations at once
suspend fun resetAnimationState(animState: CardAnimationState) {
    // Cancel any ongoing animations first
    animState.cancelAnimations()

    animState.apply {
        coroutineScope {
            // Batch all snapTo operations
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

// Track animation jobs for proper cancellation
suspend fun returnToCenter(animState: CardAnimationState) {
    // Cancel any ongoing animations
    animState.cancelAnimations()

    animState.apply {
        coroutineScope {
            val returnSpec = EnhancedCardAnimations.ReturnAnimationSpec
            val scaleSpec = EnhancedCardAnimations.ScaleAnimationSpec

            // Store jobs for potential cancellation
            val jobs = listOf(
                launch { offsetX.animateTo(0f, returnSpec) },
                launch { offsetY.animateTo(0f, returnSpec) },
                launch { rotation.animateTo(0f, returnSpec) },
                launch { scale.animateTo(1f, scaleSpec) },
                launch { blur.animateTo(0f, returnSpec) }
            )

            // Wait for all animations to complete
            jobs.joinAll()
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
    val blur: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope
) {
    // Store active animation jobs for cancellation
    private val activeJobs = mutableListOf<Job>()

    // Cancel all ongoing animations (non-suspend)
    fun cancelAnimations() {
        synchronized(activeJobs) {
            activeJobs.forEach { it.cancel() }
            activeJobs.clear()
        }
    }

    // Cleanup method to stop all Animatables
    fun cleanup() {
        cancelAnimations()
        // Launch cleanup in the provided scope
        scope.launch {
            // Stop all animations immediately
            offsetX.stop()
            offsetY.stop()
            rotation.stop()
            scale.stop()
            rotationY.stop()
            alpha.stop()
            blur.stop()
        }
    }
}