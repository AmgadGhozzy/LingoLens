package com.venom.ui.components.inputs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.venom.ui.components.common.adp
import kotlinx.coroutines.delay

/**
 * A text field that displays a typed response from a chatbot or another
 * artificial intelligence. The text is displayed character by character
 * with a typing animation. The field is read-only and can be used to
 * represent the chatbot's response in a conversation.
 *
 * @param text The text to be displayed
 * @param isTyping Whether to display the text with a typing animation
 * @param placeHolderText Optional placeholder text
 * @param maxLines Maximum number of lines for the text field
 * @param minLines Minimum number of lines for the text field
 * @param minFontSize Minimum font size for the text
 * @param maxFontSize Maximum font size for the text
 * @param minHeight Minimum height for the text field
 * @param maxHeight Maximum height for the text field
 * @param modifier Additional modifier for customization
 * @param onTypingComplete Callback when the typing animation is complete
 */
@Composable
fun AnimatedResponseTextField(
    text: String,
    isTyping: Boolean = true,
    placeHolderText: String = "",
    maxLines: Int = 12,
    minLines: Int = 1,
    minFontSize: Int = 16,
    maxFontSize: Int = 24,
    minHeight: Dp = 56.adp,
    maxHeight: Dp = 160.adp,
    modifier: Modifier = Modifier,
    onTypingComplete: () -> Unit = {}
) {
    var displayedText by remember { mutableStateOf("") }
    var showCursor by remember { mutableStateOf(false) }

    val typingSpeed = when {
        text.length > 500 -> 10L
        text.length > 200 -> 15L
        else -> 30L
    }

    LaunchedEffect(text, isTyping) {
        if (isTyping) {
            showCursor = true
            displayedText = ""
            text.forEachIndexed { i, _ ->
                delay(typingSpeed)
                displayedText = text.substring(0, i + 1)
            }
            onTypingComplete()
            showCursor = false
        } else {
            displayedText = text
            showCursor = false
        }
    }

    DynamicTextField(
        value = if (showCursor) "$displayedText ⬤" else displayedText,
        onValueChange = { },
        readOnly = true,
        placeHolderText = placeHolderText,
        maxLines = maxLines,
        minLines = minLines,
        minFontSize = minFontSize,
        maxFontSize = maxFontSize,
        minHeight = minHeight,
        maxHeight = maxHeight,
        modifier = modifier
    )
}

@Preview
@Composable
fun PreviewAnimatedResponseTextField() {
    AnimatedResponseTextField(
        text = "This is an AI response with typing animation.",
        placeHolderText = "AI is thinking...",
        isTyping = false
    )
}