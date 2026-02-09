package com.venom.ui.components.other

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

/**
 * Confetti animation view with multiple animation types.
 *
 * Supports:
 * - Lottie animations (JSON-based, smooth, customizable)
 * - Konfetti particle system (physics-based confetti)
 *
 * @param modifier Modifier to be applied to the container
 * @param animationType Type of confetti animation to display
 * @param lottieRes Raw resource ID for Lottie animation (if using Lottie)
 * @param iterations Number of times to play animation (LottieConstants.IterateForever for infinite)
 */
@Composable
fun ConfettiView(
    modifier: Modifier = Modifier,
    animationType: ConfettiAnimationType = ConfettiAnimationType.BOTH,
    lottieRes: Int? = null,
    iterations: Int = 2
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (animationType) {
            ConfettiAnimationType.LOTTIE -> {
                if (lottieRes != null) {
                    LottieConfetti(
                        lottieRes = lottieRes,
                        iterations = iterations,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            ConfettiAnimationType.KONFETTI -> {
                KonfettiConfetti(modifier = Modifier.fillMaxSize())
            }
            ConfettiAnimationType.BOTH -> {
                // Stack both animations
                KonfettiConfetti(modifier = Modifier.fillMaxSize())
                if (lottieRes != null) {
                    LottieConfetti(
                        lottieRes = lottieRes,
                        iterations = iterations,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun LottieConfetti(
    lottieRes: Int,
    iterations: Int,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(lottieRes)
    )

    LottieAnimation(
        composition = composition,
        iterations = iterations,
        modifier = modifier
    )
}

@Composable
private fun KonfettiConfetti(modifier: Modifier = Modifier) {
    val colors = listOf(
        0xFFE91E63.toInt(), // Pink
        0xFF9C27B0.toInt(), // Purple
        0xFF673AB7.toInt(), // Deep Purple
        0xFF3F51B5.toInt(), // Indigo
        0xFF2196F3.toInt(), // Blue
        0xFF03DAC6.toInt(), // Teal
        0xFF4CAF50.toInt(), // Green
        0xFFFF9800.toInt()  // Orange
    )

    KonfettiView(
        modifier = modifier,
        parties = listOf(
            Party(
                speed = 40f,
                maxSpeed = 60f,
                damping = 0.9f,
                spread = 360,
                colors = colors,
                position = Position.Relative(0.5, 0.4),
                emitter = Emitter(duration = 1000, TimeUnit.MILLISECONDS).max(200)
            )
        )
    )
}

@Composable
fun CustomConfettiView(
    modifier: Modifier = Modifier,
    colors: List<Int> = getDefaultConfettiColors(),
    particleCount: Int = 200,
    duration: Long = 1000,
    speed: Float = 40f,
    maxSpeed: Float = 60f,
    damping: Float = 0.9f,
    spread: Int = 360,
    position: Position = Position.Relative(0.5, 0.4)
) {
    KonfettiView(
        modifier = modifier,
        parties = listOf(
            Party(
                speed = speed,
                maxSpeed = maxSpeed,
                damping = damping,
                spread = spread,
                colors = colors,
                position = position,
                emitter = Emitter(duration = duration, TimeUnit.MILLISECONDS).max(particleCount)
            )
        )
    )
}

/**
 * Confetti animation type.
 */
enum class ConfettiAnimationType {
    /** Lottie JSON-based animation (smooth, lightweight) */
    LOTTIE,

    /** Konfetti particle system (physics-based) */
    KONFETTI,

    /** Both Lottie and Konfetti combined */
    BOTH
}

/**
 * Get default Material Design confetti colors.
 */
private fun getDefaultConfettiColors(): List<Int> = listOf(
    0xFFE91E63.toInt(), // Pink
    0xFF9C27B0.toInt(), // Purple
    0xFF673AB7.toInt(), // Deep Purple
    0xFF3F51B5.toInt(), // Indigo
    0xFF2196F3.toInt(), // Blue
    0xFF03DAC6.toInt(), // Teal
    0xFF4CAF50.toInt(), // Green
    0xFFFF9800.toInt(), // Orange
    0xFFFFEB3B.toInt(), // Yellow
    0xFFFF5722.toInt()  // Deep Orange
)