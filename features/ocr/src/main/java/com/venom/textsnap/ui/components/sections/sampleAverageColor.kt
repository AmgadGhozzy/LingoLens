package com.venom.textsnap.ui.components.sections

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb

data class TextColors(val backgroundColor: Color, val textColor: Color)

fun sampleTextColors(imageBitmap: ImageBitmap, rect: Rect): TextColors {
    val bitmap = imageBitmap.asAndroidBitmap()

    return try {
        // Map rect coordinates to bitmap
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

        // Sample all pixels in the bounding box
        val pixels = IntArray((endX - startX + 1) * (endY - startY + 1))
        var pixelIndex = 0
        for (x in startX..endX) {
            for (y in startY..endY) {
                pixels[pixelIndex++] = bitmap.getPixel(x, y)
            }
        }

        // Find most frequent background color
        val backgroundColor =
            pixels.groupBy { it }.maxByOrNull { it.value.size }?.key ?: pixels.first()

        // Text color detection
        val textColorCandidates = pixels.filter { pixel ->
            // Filter for pixels significantly different from background
            colorDifference(pixel, backgroundColor) > 5000
        }

        // Select text color with highest contrast
        val textColor = textColorCandidates.maxByOrNull { pixel ->
            calculateColorContrast(Color(pixel), Color(backgroundColor))
        } ?: (if (Color(backgroundColor).luminance() > 0.5) Color.Black.toArgb() else Color.White.toArgb())

        TextColors(
            backgroundColor = Color(backgroundColor), textColor = Color(textColor)
        )
    } catch (_: Exception) {
        TextColors(Color.White, Color.Black)
    }
}

// Calculate color contrast ratio
private fun calculateColorContrast(c1: Color, c2: Color): Float {
    val l1 = c1.luminance()
    val l2 = c2.luminance()
    return if (l1 > l2) (l1 + 0.05f) / (l2 + 0.05f) else (l2 + 0.05f) / (l1 + 0.05f)
}

// Calculate color difference
private fun colorDifference(c1: Int, c2: Int): Int {
    val r = (c1 shr 16 and 0xFF) - (c2 shr 16 and 0xFF)
    val g = (c1 shr 8 and 0xFF) - (c2 shr 8 and 0xFF)
    val b = (c1 and 0xFF) - (c2 and 0xFF)
    return r * r + g * g + b * b
}
