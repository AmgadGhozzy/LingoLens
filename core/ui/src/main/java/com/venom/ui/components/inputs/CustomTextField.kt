package com.venom.ui.components.inputs

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.venom.ui.components.common.adp

/**
 * A customizable text field supporting multiple languages and text directions
 * with a scrollbar and dynamic font sizing.
 *
 * @param textValue The current text value
 * @param onTextChange Callback when text changes
 * @param isReadOnly Whether the text field is editable
 * @param placeHolderText Optional placeholder text
 * @param maxLines Maximum number of lines for the text field
 * @param minLines Minimum number of lines for the text field
 * @param minFontSize Minimum font size for the text
 * @param maxFontSize Maximum font size for the text
 * @param minHeight Minimum height of the text field
 * @param maxHeight Maximum height of the text field
 * @param modifier Additional modifier for customization
 *
 * @see [TextField] for a simpler text field without scrollbar and dynamic font sizing
 */
@Composable
fun CustomTextField(
    textValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit = {},
    isReadOnly: Boolean = false,
    placeHolderText: String = "",
    maxLines: Int = 12,
    minLines: Int = 1,
    minFontSize: Int = 16,
    maxFontSize: Int = 32,
    minHeight: Dp = 56.adp,
    maxHeight: Dp = 160.adp,
    modifier: Modifier = Modifier
) {
    DynamicTextField(
        value = textValue.text,
        onValueChange = { onTextChange(TextFieldValue(it, textValue.selection)) },
        readOnly = isReadOnly,
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
fun PreviewCustomTextField() {
    CustomTextField(
        textValue = TextFieldValue("User input text field"),
        placeHolderText = "Type something...",
    )
}
