package com.venom.textsnap.ui.components.sections

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap

data class TextColors(val backgroundColor: Color, val textColor: Color)

fun sampleTextColors(imageBitmap: ImageBitmap, rect: Rect): TextColors {
    val bitmap = imageBitmap.asAndroidBitmap()

    try {
        // Calculate sampling coordinates
        val startX =
            ((rect.left * bitmap.width) / imageBitmap.width).toInt().coerceIn(0, bitmap.width - 1)
        val startY =
            ((rect.top * bitmap.height) / imageBitmap.height).toInt().coerceIn(0, bitmap.height - 1)
        val endX =
            ((rect.right * bitmap.width) / imageBitmap.width).toInt().coerceIn(0, bitmap.width - 1)
        val endY = ((rect.bottom * bitmap.height) / imageBitmap.height).toInt()
            .coerceIn(0, bitmap.height - 1)

        // Early exit for tiny areas
        if (endX - startX < 2 || endY - startY < 2) {
            return TextColors(Color.White, Color.Black)
        }

        // Enhanced sampling strategy
        val width = endX - startX
        val height = endY - startY

        // Sample edges with better coverage
        val sampleStep = maxOf(minOf(width, height) / 12, 1) // Increased sampling density
        val edgePixels = IntArray(48) // Increased array size for more samples
        var idx = 0

        // Sample top, bottom, left, and right edges
        for (x in startX..endX step sampleStep) {
            if (idx < edgePixels.size - 3) {
                edgePixels[idx++] = bitmap.getPixel(x, startY)
                edgePixels[idx++] = bitmap.getPixel(x, endY)
            }
        }

        for (y in startY..endY step sampleStep) {
            if (idx < edgePixels.size - 1) {
                edgePixels[idx++] = bitmap.getPixel(startX, y)
                edgePixels[idx++] = bitmap.getPixel(endX, y)
            }
        }

        val backgroundColor = findDominantColor(edgePixels, idx)

        // Enhanced center sampling for text color
        val centerX = (startX + endX) / 2
        val centerY = (startY + endY) / 2
        val radius = minOf(width, height) / 3 // Increased radius for better coverage

        var bestContrast = 1.5f
        var textColor = backgroundColor

        val centerStep = maxOf(radius / 4, 1) // Smaller steps for more samples
        for (x in (centerX - radius)..(centerX + radius) step centerStep) {
            for (y in (centerY - radius)..(centerY + radius) step centerStep) {
                val pixel = bitmap.getPixel(
                    x.coerceIn(startX, endX), y.coerceIn(startY, endY)
                )

                val contrast = calculateContrast(pixel, backgroundColor)
                if (contrast > bestContrast) {
                    bestContrast = contrast
                    textColor = pixel
                    if (contrast > 3.0f) break // Early exit on good contrast
                }
            }
        }

        return TextColors(
            backgroundColor = Color(backgroundColor), textColor = Color(textColor)
        )
    } catch (_: Exception) {
        return TextColors(Color.White, Color.Black)
    }
}

private fun findDominantColor(colors: IntArray, count: Int): Int {
    if (count == 0) return 0xFFFFFFFF.toInt()

    var maxFreq = 0
    var dominant = colors[0]

    for (i in 0 until count) {
        var freq = 1
        val current = colors[i]

        for (j in i + 1 until count) {
            if (colorDifference(current, colors[j]) < 2500) freq++
        }

        if (freq > maxFreq) {
            maxFreq = freq
            dominant = current
        }
    }

    return dominant
}

private fun colorDifference(c1: Int, c2: Int): Int {
    val r = (c1 shr 16 and 0xFF) - (c2 shr 16 and 0xFF)
    val g = (c1 shr 8 and 0xFF) - (c2 shr 8 and 0xFF)
    val b = (c1 and 0xFF) - (c2 and 0xFF)
    return r * r + g * g + b * b
}

private fun calculateContrast(c1: Int, c2: Int): Float {
    val l1 = getLuminance(c1)
    val l2 = getLuminance(c2)
    return if (l1 > l2) (l1 + 0.05f) / (l2 + 0.05f) else (l2 + 0.05f) / (l1 + 0.05f)
}

private fun getLuminance(color: Int): Float {
    val r = (color shr 16 and 0xFF) / 255f
    val g = (color shr 8 and 0xFF) / 255f
    val b = (color and 0xFF) / 255f
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}
