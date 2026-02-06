package com.venom.stackcard.ui.components.flashcard

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.venom.ui.theme.BrandColors
import kotlin.math.abs

/**
 * Pre-allocated color constants for card borders and overlays.
 * Avoids Color.copy
 */
@Stable
object CardColors {
    // Neutral state
    val NeutralBorder = Color(0x33808080)
    val NeutralBorderDark = Color(0x33FFFFFF)

    // Remember (right swipe) gradient stops
    val RememberBorderMin = BrandColors.Green500.copy(alpha = 0.3f)
    val RememberBorderMax = BrandColors.Green500.copy(alpha = 0.7f)

    // Forgot (left swipe) gradient stops
    val ForgotBorderMin = BrandColors.Red500.copy(alpha = 0.3f)
    val ForgotBorderMax = BrandColors.Red500.copy(alpha = 0.7f)

    // Threshold for color transition start
    const val PROGRESS_THRESHOLD = 0.3f

    /**
     * Calculate border color based on swipe progress
     * Returns pre-allocated color when below thresholdl
     */
    fun getBorderColor(progress: Float, isDarkTheme: Boolean = false): Color {
        val absProgress = abs(progress)

        return when {
            absProgress < PROGRESS_THRESHOLD -> {
                if (isDarkTheme) NeutralBorderDark else NeutralBorder
            }

            progress > 0 -> {
                val t = ((absProgress - PROGRESS_THRESHOLD) / (1f - PROGRESS_THRESHOLD))
                    .coerceIn(0f, 1f)
                lerp(RememberBorderMin, RememberBorderMax, t)
            }

            else -> {
                val t = ((absProgress - PROGRESS_THRESHOLD) / (1f - PROGRESS_THRESHOLD))
                    .coerceIn(0f, 1f)
                lerp(ForgotBorderMin, ForgotBorderMax, t)
            }
        }
    }

    /**
     * Get border width based on progress.
     */
    fun getBorderWidth(progress: Float): Float {
        return if (abs(progress) > PROGRESS_THRESHOLD) 2f else 1f
    }
}