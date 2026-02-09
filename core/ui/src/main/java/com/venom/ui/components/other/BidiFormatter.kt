package com.venom.ui.components.other

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection

/**
 * BiDi text formatter for mixed Arabic-English
 */
object BiDiFormatter {
    private const val LRI = '\u2066' // Left-to-Right Isolate
    private const val RLI = '\u2067' // Right-to-Left Isolate
    private const val PDI = '\u2069' // Pop Directional Isolate

    private fun isArabic(c: Char) = c.code in 0x0600..0x06FF
    private fun isEnglish(c: Char) = c in 'A'..'Z' || c in 'a'..'z' || c in '0'..'9'

    // Detect direction of text content (ignoring brackets/punctuation)
    private fun getDirection(text: String): Char? {
        for (c in text) {
            if (isArabic(c)) return 'A'
            if (isEnglish(c)) return 'E'
        }
        return null
    }

    fun format(text: String): String {
        val result = StringBuilder()
        var i = 0
        var currentDir: Char? = null

        while (i < text.length) {
            val c = text[i]

            when {
                c == '(' -> {
                    // Find matching closing bracket
                    val closeIndex = text.indexOf(')', i)
                    if (closeIndex != -1) {
                        val insideContent = text.substring(i + 1, closeIndex)
                        val insideDir = getDirection(insideContent)

                        // Close current segment
                        if (currentDir != null) {
                            result.append(PDI)
                            currentDir = null
                        }

                        // Wrap entire bracketed section
                        if (insideDir == 'A') {
                            result.append(RLI).append('(').append(insideContent).append(')')
                                .append(PDI)
                        } else {
                            result.append(LRI).append('(').append(insideContent).append(')')
                                .append(PDI)
                        }

                        i = closeIndex + 1
                        continue
                    } else {
                        // No closing bracket, treat normally
                        result.append(c)
                    }
                }

                isArabic(c) -> {
                    if (currentDir == 'E') {
                        result.append(PDI)
                    }
                    if (currentDir != 'A') {
                        result.append(RLI)
                        currentDir = 'A'
                    }
                    result.append(c)
                }

                isEnglish(c) -> {
                    if (currentDir == 'A') {
                        result.append(PDI)
                    }
                    if (currentDir != 'E') {
                        result.append(LRI)
                        currentDir = 'E'
                    }
                    result.append(c)
                }

                else -> {
                    result.append(c)
                }
            }
            i++
        }

        if (currentDir != null) result.append(PDI)
        return result.toString()
    }
}

@Composable
fun BiDiText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = BiDiFormatter.format(text),
        modifier = modifier,
        style = TextStyle(
            textDirection = TextDirection.Content
        )
    )
}