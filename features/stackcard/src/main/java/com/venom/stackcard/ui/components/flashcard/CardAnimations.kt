package com.venom.stackcard.ui.components.flashcard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

/**
 * Pre-instantiated animation specs and physics constants.
 */
object OptimizedCardAnimations {
    const val SWIPE_THRESHOLD_DP = 150
    const val MAX_ROTATION_DEGREES = 15f
    const val THROW_TARGET_BASE = 1200f
    const val THROW_TARGET_Y = -100f

    private const val ROTATION_MULTIPLIER = 0.04f
    private const val VELOCITY_ROTATION_MULTIPLIER = 0.0008f
    private const val VELOCITY_THROW_MULTIPLIER = 0.3f
    private const val VELOCITY_THROW_CLAMP = 400f
    val SwipeAnimationSpec: AnimationSpec<Float> = spring(
        dampingRatio = 0.75f,
        stiffness = Spring.StiffnessMediumLow,
        visibilityThreshold = 1f
    )

    val ReturnAnimationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow,
        visibilityThreshold = 0.01f
    )

    val FlipAnimationSpec: AnimationSpec<Float> = spring(
        dampingRatio = 0.65f,
        stiffness = Spring.StiffnessMediumLow
    )

    /**
     * Calculate rotation based on horizontal offset and velocity
     */
    fun calculateRotation(offsetX: Float, velocity: Float = 0f): Float {
        val baseRotation = (offsetX * ROTATION_MULTIPLIER)
            .coerceIn(-MAX_ROTATION_DEGREES, MAX_ROTATION_DEGREES)
        val velocityInfluence = (velocity * VELOCITY_ROTATION_MULTIPLIER).coerceIn(-5f, 5f)
        return baseRotation + velocityInfluence
    }

    /**
     * Calculate throw target for swipe-off animation
     */
    fun calculateThrowTarget(currentOffset: Float, velocity: Float): Float {
        val baseTarget = if (currentOffset > 0) THROW_TARGET_BASE else -THROW_TARGET_BASE
        val velocityContribution = (velocity * VELOCITY_THROW_MULTIPLIER)
            .coerceIn(-VELOCITY_THROW_CLAMP, VELOCITY_THROW_CLAMP)
        return baseTarget + velocityContribution
    }
}

/**
 * Stable animation state container that avoids recomposition triggers
 */
@Stable
class CardAnimationState(
    val offsetX: Animatable<Float, AnimationVector1D>,
    val offsetY: Animatable<Float, AnimationVector1D>,
    val rotation: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope
) {
    private val currentJob = AtomicReference<Job?>(null)

    @Volatile
    private var isDisposed = false

    /**
     * Fast path for drag updates. Uses UNDISPATCHED start to execute
     * synchronously when possible, avoiding coroutine scheduling overhead.
     *
     * This is called 60-120 times per second during drag, so performance
     * is critical.
     */
    fun updateDragPosition(deltaX: Float, deltaY: Float) {
        if (isDisposed) return

        // Cancel any running animation
        currentJob.getAndSet(null)?.cancel()

        val newX = offsetX.value + deltaX
        val newY = offsetY.value + deltaY

        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                offsetX.snapTo(newX)
                offsetY.snapTo(newY)
                rotation.snapTo(OptimizedCardAnimations.calculateRotation(newX))
            } catch (_: CancellationException) {
            }
        }
    }

    /**
     * Animate card off-screen after successful swipe.
     */
    fun animateSwipeOff(
        targetX: Float,
        targetY: Float = OptimizedCardAnimations.THROW_TARGET_Y,
        onComplete: () -> Unit
    ) {
        if (isDisposed) return

        val newJob = scope.launch {
            try {
                coroutineScope {
                    launch {
                        offsetX.animateTo(targetX, OptimizedCardAnimations.SwipeAnimationSpec)
                    }
                    launch {
                        offsetY.animateTo(targetY, OptimizedCardAnimations.SwipeAnimationSpec)
                    }
                }
                onComplete()
            } catch (_: CancellationException) {
            }
        }

        // Atomically replace and cancel previous job
        currentJob.getAndSet(newJob)?.cancel()
    }

    /**
     * Animate card back to center after incomplete swipe.
     */
    fun animateReturnToCenter() {
        if (isDisposed) return

        val spec = OptimizedCardAnimations.ReturnAnimationSpec

        val newJob = scope.launch {
            try {
                coroutineScope {
                    launch { offsetX.animateTo(0f, spec) }
                    launch { offsetY.animateTo(0f, spec) }
                    launch { rotation.animateTo(0f, spec) }
                }
            } catch (_: CancellationException) {
            }
        }

        currentJob.getAndSet(newJob)?.cancel()
    }

    suspend fun resetImmediate() {
        currentJob.getAndSet(null)?.cancel()

        try {
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
            rotation.snapTo(0f)
        } catch (_: CancellationException) {
            // Ignore
        }
    }

    /**
     * Cancel any running animations.
     */
    fun cancelAnimations() {
        currentJob.getAndSet(null)?.cancel()
    }

    /**
     * Full cleanup. Must be called when composable leaves composition.
     */
    fun cleanup() {
        isDisposed = true
        cancelAnimations()

        // Stop animations immediately
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            runCatching {
                offsetX.stop()
                offsetY.stop()
                rotation.stop()
            }
        }
    }
}

/**
 * Remember and manage CardAnimationState lifecycle.
 */
@Composable
fun rememberCardAnimationState(): CardAnimationState {
    val scope = rememberCoroutineScope()

    val state = remember {
        CardAnimationState(
            offsetX = Animatable(0f),
            offsetY = Animatable(0f),
            rotation = Animatable(0f),
            scope = scope
        )
    }

    DisposableEffect(state) {
        onDispose {
            state.cleanup()
        }
    }
    return state
}

/**
 * Stack position data class for card positioning
 */
@Stable
data class StackPosition(
    val offsetYDp: Float,
    val offsetXDp: Float,
    val rotation: Float,
    val scale: Float
)