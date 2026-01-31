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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.sqrt

/** Pre-instantiated animation specs and physics constants */
object OptimizedCardAnimations {
    const val SWIPE_THRESHOLD_DP = 150
    const val MAX_ROTATION_DEGREES = 15f

    private const val ROTATION_MULTIPLIER = 0.04f
    private const val VELOCITY_MULTIPLIER = 0.0008f

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

    fun calculateRotation(offsetX: Float, velocity: Float = 0f): Float {
        val baseRotation = (offsetX * ROTATION_MULTIPLIER)
            .coerceIn(-MAX_ROTATION_DEGREES, MAX_ROTATION_DEGREES)
        val velocityInfluence = (velocity * VELOCITY_MULTIPLIER).coerceIn(-5f, 5f)
        return baseRotation + velocityInfluence
    }

    fun calculateThrowTarget(currentOffset: Float, velocity: Float): Float {
        val baseTarget = if (currentOffset > 0) 1200f else -1200f
        val velocityContribution = (velocity * 0.3f).coerceIn(-400f, 400f)
        return baseTarget + velocityContribution
    }
}

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
        onDispose { state.cleanup() }
    }

    return state
}

suspend fun resetAnimationState(animState: CardAnimationState) {
    animState.cancelAnimations()
    animState.offsetX.snapTo(0f)
    animState.offsetY.snapTo(0f)
    animState.rotation.snapTo(0f)
}

fun returnToCenter(animState: CardAnimationState) {
    animState.cancelAnimations()
    val spec = OptimizedCardAnimations.ReturnAnimationSpec
    animState.animateOffsetX(0f, spec)
    animState.animateOffsetY(0f, spec)
    animState.animateRotation(0f, spec)
}

@Stable
data class CardAnimationState(
    val offsetX: Animatable<Float, AnimationVector1D>,
    val offsetY: Animatable<Float, AnimationVector1D>,
    val rotation: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope
) {
    private val activeJobs = mutableListOf<Job>()
    @Volatile
    private var isDisposed = false

    fun animateOffsetX(target: Float, spec: AnimationSpec<Float>) {
        launchTracked { offsetX.animateTo(target, spec) }
    }

    fun animateOffsetY(target: Float, spec: AnimationSpec<Float>) {
        launchTracked { offsetY.animateTo(target, spec) }
    }

    fun animateRotation(target: Float, spec: AnimationSpec<Float>) {
        launchTracked { rotation.animateTo(target, spec) }
    }

    fun calculateScale(): Float {
        val dragDistance = sqrt(offsetX.value * offsetX.value + offsetY.value * offsetY.value)
        return (1f - (dragDistance * 0.0003f)).coerceIn(0.8f, 1f)
    }

    private fun launchTracked(block: suspend () -> Unit): Job? {
        if (isDisposed) return null
        val job = scope.launch {
            try {
                block()
            } finally {
                synchronized(activeJobs) { activeJobs.remove(coroutineContext[Job]) }
            }
        }
        synchronized(activeJobs) {
            if (isDisposed) {
                job.cancel(); return null
            }
            activeJobs.add(job)
        }
        return job
    }

    fun cancelAnimations() {
        synchronized(activeJobs) {
            activeJobs.forEach { it.cancel() }
            activeJobs.clear()
        }
    }

    fun cleanup() {
        isDisposed = true
        cancelAnimations()
        scope.launch {
            runCatching {
                offsetX.stop()
                offsetY.stop()
                rotation.stop()
            }
        }
    }
}