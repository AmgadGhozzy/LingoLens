package com.venom.textsnap.ui.components.sections

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb

data class TextColors(val backgroundColor: Color, val textColor: Color)

private val DEFAULT_COLORS = TextColors(Color.White, Color.Black)
private const val COLOR_DIFF_THRESHOLD = 5000
private const val MAX_SAMPLES = 64

fun sampleTextColors(imageBitmap: ImageBitmap, rect: Rect): TextColors {
    val bitmap = imageBitmap.asAndroidBitmap()

    return try {
        val startX = ((rect.left * bitmap.width) / imageBitmap.width).toInt().coerceIn(0, bitmap.width - 1)
        val startY = ((rect.top * bitmap.height) / imageBitmap.height).toInt().coerceIn(0, bitmap.height - 1)
        val endX = ((rect.right * bitmap.width) / imageBitmap.width).toInt().coerceIn(0, bitmap.width - 1)
        val endY = ((rect.bottom * bitmap.height) / imageBitmap.height).toInt().coerceIn(0, bitmap.height - 1)

        val w = endX - startX + 1
        val h = endY - startY + 1
        if (w < 2 || h < 2) return DEFAULT_COLORS

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, startX, startY, w, h)

        val step = maxOf(1, pixels.size / MAX_SAMPLES)
        val sampled = IntArray(pixels.size / step + 1).also { arr ->
            var idx = 0
            for (i in pixels.indices step step) arr[idx++] = pixels[i]
        }

        val bgColor = sampled.toList()
            .groupingBy { it }.eachCount()
            .maxByOrNull { it.value }?.key ?: sampled[0]

        val textColor = sampled
            .filter { colorDifference(it, bgColor) > COLOR_DIFF_THRESHOLD }
            .maxByOrNull { contrastRatio(Color(it), Color(bgColor)) }
            ?: if (Color(bgColor).luminance() > 0.5f) Color.Black.toArgb() else Color.White.toArgb()

        TextColors(Color(bgColor), Color(textColor))
    } catch (_: Exception) {
        DEFAULT_COLORS
    }
}

private fun contrastRatio(c1: Color, c2: Color): Float {
    val l1 = c1.luminance()
    val l2 = c2.luminance()
    return if (l1 > l2) (l1 + 0.05f) / (l2 + 0.05f) else (l2 + 0.05f) / (l1 + 0.05f)
}

private fun colorDifference(c1: Int, c2: Int): Int {
    val r = (c1 shr 16 and 0xFF) - (c2 shr 16 and 0xFF)
    val g = (c1 shr 8 and 0xFF) - (c2 shr 8 and 0xFF)
    val b = (c1 and 0xFF) - (c2 and 0xFF)
    return r * r + g * g + b * b
}