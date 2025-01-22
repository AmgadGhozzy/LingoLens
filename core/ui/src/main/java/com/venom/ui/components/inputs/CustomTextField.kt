package com.venom.ui.components.inputs

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.resources.R
import com.venom.utils.getTextDirection

/**
 * A customizable text field supporting multiple languages and text directions
 * with a scrollbar and dynamic font sizing.
 *
 * @param textValue The current text value
 * @param onTextChange Callback when text changes
 * @param isReadOnly Whether the text field is editable
 * @param placeHolderText Optional placeholder text
 * @param minHeight Minimum height of the text field
 * @param maxHeight Maximum height of the text field
 * @param minFontSize Minimum font size for the text
 * @param maxFontSize Maximum font size for the text
 * @param colors Custom colors for the text field
 * @param modifier Additional modifier for customization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    textValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit = {},
    isReadOnly: Boolean = false,
    placeHolderText: String = stringResource(R.string.type_something),
    maxLines: Int = 12,
    minLines: Int = 1,

    minFontSize: Int = 14,
    maxFontSize: Int = 22,

    minHeight: Dp = 48.dp,
    maxHeight: Dp = 148.dp,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,

        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,

        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    ),
    modifier: Modifier = Modifier
) {
    val dynamicFontSize = remember(textValue.text) {
        (maxFontSize - (textValue.text.length / 50)).coerceAtLeast(minFontSize).sp
    }

    val scrollState = remember { ScrollState(0) }

    TextField(
        modifier = modifier
            .heightIn(min = minHeight, max = maxHeight)
            .verticalScroll(scrollState),
        value = textValue.text,
        readOnly = isReadOnly,
        maxLines = maxLines,
        minLines = minLines,
        onValueChange = { newText ->
            onTextChange(TextFieldValue(newText, textValue.selection))
        },
        textStyle = TextStyle(
            fontSize = dynamicFontSize, textDirection = getTextDirection(textValue.text)
        ),
        placeholder = {
            Text(
                text = placeHolderText,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontSize = 20.sp,
                style = TextStyle(textDirection = getTextDirection(textValue.text))
            )
        },
        colors = colors
    )
}

@Preview
@Composable
fun PreviewCustomTextFieldLTR() {
    CustomTextField(
        textValue = TextFieldValue("This is a longer text to test the dynamic font size and scrolling functionality."),
        placeHolderText = "Type something...",
    )
}

@Preview
@Composable
fun PreviewCustomTextFieldRTL() {
    CustomTextField(
        textValue = TextFieldValue("هذه جملة لاختبار حجم الخط الديناميكي والتمرير."),
        placeHolderText = "اكتب شيئا...",
        isReadOnly = true,
    )
}