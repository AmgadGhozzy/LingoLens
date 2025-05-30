package com.venom.ui.components.inputs

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.utils.getTextDirection
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedResponseTextField(
    text: String,
    isTyping: Boolean = true,
    placeHolderText: String = "",
    maxLines: Int = 12,
    minLines: Int = 1,
    minFontSize: Int = 14,
    maxFontSize: Int = 22,
    minHeight: Dp = 48.dp,
    maxHeight: Dp = 148.dp,
    modifier: Modifier = Modifier,
    onTypingComplete: () -> Unit = {}
) {
    var displayedText by remember { mutableStateOf("") }
    var showCursor by remember { mutableStateOf(false) }
    val scrollState = remember { ScrollState(0) }

    val dynamicFontSize = (maxFontSize - (displayedText.length / 50)).coerceAtLeast(minFontSize).sp
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
                scrollState.scrollTo(scrollState.maxValue)
            }
            onTypingComplete()
            showCursor = false
        } else {
            displayedText = text
            showCursor = false
        }
    }

    TextField(
        modifier = modifier
            .heightIn(min = minHeight, max = maxHeight)
            .verticalScroll(scrollState),
        value = if (showCursor) "$displayedText●" else displayedText,
        onValueChange = { },
        readOnly = true,
        maxLines = maxLines,
        minLines = minLines,
        textStyle = TextStyle(fontSize = dynamicFontSize, textDirection = getTextDirection(displayedText)),
        placeholder = {
            Text(
                text = placeHolderText,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontSize = 20.sp,
                style = TextStyle(textDirection = getTextDirection(displayedText))
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}
