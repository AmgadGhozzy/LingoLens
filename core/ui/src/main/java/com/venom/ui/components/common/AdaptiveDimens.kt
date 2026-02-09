package com.venom.ui.components.common

/**
 * Adaptive Dimensions for Jetpack Compose
 *
 * Automatically scales UI elements to different screen sizes using logarithmic scaling.
 *
 * Usage: 16.adp for dimensions, 18.asp for text sizes
 */

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ln

// Base reference width (standard phone width)
private const val BASE_WIDTH = 360f

// Base aspect ratio (16:9)
private const val BASE_ASPECT_RATIO = 1.78f

// Logarithmic scaling weight
private const val LOG_WEIGHT = 0.6f

// Aspect ratio adjustment weight
private const val AR_WEIGHT = 0.00267f

/**
 * Global cache for adaptive dimension calculations.
 * Thread-safe with O(1) lookup time.
 */
private object Cache {
    private val map = ConcurrentHashMap<Int, Float>()

    /** Get cached value or calculate if not exists */
    fun get(baseDp: Float, width: Int, height: Int): Float {
        val key = hash(baseDp, width, height)
        return map.getOrPut(key) {
            calculate(baseDp, width.toFloat(), height.toFloat())
        }
    }

    /** Generate hash key from inputs */
    private fun hash(baseDp: Float, width: Int, height: Int): Int {
        var result = baseDp.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }

    /** Calculate adaptive dimension using logarithmic scaling */
    private fun calculate(base: Float, width: Float, height: Float): Float {
        val widthRatio = width / BASE_WIDTH
        val logScale = ln(widthRatio + 1) * LOG_WEIGHT + (1 - LOG_WEIGHT)

        val aspectRatio = height / width
        val arAdjust = 1f + AR_WEIGHT * ln(aspectRatio / BASE_ASPECT_RATIO)

        return base * logScale * arAdjust
    }
}

/** Convert Int to adaptive Dp (e.g., 16.adp) */
val Int.adp: Dp
    @Composable
    get() {
        val config = LocalConfiguration.current
        return Cache.get(this.toFloat(), config.screenWidthDp, config.screenHeightDp).dp
    }

/** Convert Float to adaptive Dp (e.g., 16.5.adp) */
val Float.adp: Dp
    @Composable
    get() {
        val config = LocalConfiguration.current
        return Cache.get(this, config.screenWidthDp, config.screenHeightDp).dp
    }

/** Convert Double to adaptive Dp (e.g., 16.5.adp) */
val Double.adp: Dp
    @Composable
    get() {
        val config = LocalConfiguration.current
        return Cache.get(this.toFloat(), config.screenWidthDp, config.screenHeightDp).dp
    }

/** Convert Int to adaptive TextUnit (e.g., 18.asp) */
val Int.asp: TextUnit
    @Composable
    get() {
        val config = LocalConfiguration.current
        return Cache.get(this.toFloat(), config.screenWidthDp, config.screenHeightDp).sp
    }

/** Convert Float to adaptive TextUnit (e.g., 18.5.asp) */
val Float.asp: TextUnit
    @Composable
    get() {
        val config = LocalConfiguration.current
        return Cache.get(this, config.screenWidthDp, config.screenHeightDp).sp
    }

/** Convert Double to adaptive TextUnit (e.g., 18.5.asp) */
val Double.asp: TextUnit
    @Composable
    get() {
        val config = LocalConfiguration.current
        return Cache.get(this.toFloat(), config.screenWidthDp, config.screenHeightDp).sp
    }
