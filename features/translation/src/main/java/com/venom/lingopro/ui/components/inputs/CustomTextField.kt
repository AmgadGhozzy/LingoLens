package com.venom.lingopro.ui.components.inputs

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.lingopro.utils.isTextRightToLeft

/**
 * A customizable text field supporting multiple languages and text directions
 * with a scrollbar and dynamic font sizing.
 *
 * @param textValue The current text value
 * @param onTextChange Callback when text changes
 * @param isReadOnly Whether the text field is editable
 * @param placeHolderText Optional placeholder text
 * @param isRightToLeft Determines text direction (true for Arabic, false for English)
 * @param modifier Additional modifier for customization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    textValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit = {},
    isReadOnly: Boolean = false,
    placeHolderText: String = "Type something...",
    modifier: Modifier = Modifier
) {
    val dynamicFontSize = remember(textValue.text) {
        when {
            textValue.text.length <= 50 -> 22.sp
            textValue.text.length <= 100 -> 20.sp
            textValue.text.length <= 200 -> 18.sp
            textValue.text.length <= 300 -> 16.sp
            else -> 14.sp
        }
    }

    val isRightToLeft = remember(textValue.text) {
        isTextRightToLeft(textValue.text)
    }

    val scrollState = remember { ScrollState(0) }

    val textDirection = if (isRightToLeft) TextDirection.Rtl else TextDirection.Ltr

    TextField(
        modifier = modifier
            .heightIn(min = 48.dp, max = 180.dp)
            .verticalScroll(scrollState),
        value = textValue.text,
        onValueChange = { newText ->
            onTextChange(TextFieldValue(newText, textValue.selection))
        },
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = dynamicFontSize,
            textDirection = textDirection
        ),
        readOnly = isReadOnly,
        placeholder = {
            Text(
                text = placeHolderText,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontSize = 20.sp,
                style = TextStyle(
                    textDirection = textDirection
                )
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
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
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
    )
}