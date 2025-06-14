package com.venom.ui.components.inputs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.utils.getTextDirection

/**
 * Base text field component with consistent styling and behavior.
 *
 * @param value The current text value
 * @param onValueChange Callback when text changes
 * @param readOnly Whether the text field is read-only
 * @param placeHolderText The placeholder text when the field is empty
 * @param maxLines Maximum number of lines for the text field
 * @param minLines Minimum number of lines for the text field
 * @param minHeight Minimum height for the text field
 * @param maxHeight Maximum height for the text field
 * @param modifier Additional modifier for customization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean,
    placeHolderText: String,
    maxLines: Int = 12,
    minLines: Int = 1,
    minFontSize: Int = 16,
    maxFontSize: Int = 24,
    minHeight: Dp = 56.dp,
    maxHeight: Dp = 160.dp,
    modifier: Modifier = Modifier
) {
    val scrollState = remember { ScrollState(0) }
    val dynamicFontSize = (maxFontSize - (value.length / 40)).coerceAtLeast(minFontSize).sp

    val textAlpha by animateFloatAsState(
        targetValue = if (value.isEmpty()) 0.7f else 1f,
        animationSpec = spring()
    )

    TextField(
        modifier = modifier
            .heightIn(min = minHeight, max = maxHeight)
            .verticalScroll(scrollState),
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        maxLines = maxLines,
        minLines = minLines,
        textStyle = TextStyle(
            fontSize = dynamicFontSize,
            textDirection = getTextDirection(value),
            fontWeight = FontWeight.Medium,
            lineHeight = (dynamicFontSize.value * 1.4).sp
        ).copy(color = LocalContentColor.current.copy(alpha = textAlpha)),
        placeholder = {
            Text(
                text = placeHolderText,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 20.sp,
                style = TextStyle(textDirection = getTextDirection(placeHolderText)),
                modifier = Modifier.fillMaxSize()
            )
        },
        shape = RoundedCornerShape(0.dp),
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