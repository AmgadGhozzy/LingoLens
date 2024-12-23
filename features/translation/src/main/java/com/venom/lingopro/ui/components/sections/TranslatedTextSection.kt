package com.venom.lingopro.ui.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.venom.resources.R
import com.venom.ui.components.inputs.CustomTextField

/**
 * A composable function that displays a translated text. The text is displayed in a [CustomTextField]
 * which is readonly and has a placeholder text.
 *
 * @param translatedTextValue the translated text to be displayed
 * @param modifier a modifier that can be used to customize the appearance of the component
 */
@Composable
fun TranslatedTextSection(
    translatedTextValue: TextFieldValue,
    modifier: Modifier = Modifier,
) {
    CustomTextField(
        textValue = translatedTextValue,
        isReadOnly = true,
        placeHolderText = stringResource(R.string.translate_result_placeholder),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    )
}

@Preview(showBackground = true)
@Composable
fun TranslatedTextSectionPreview() {
    MaterialTheme {
        TranslatedTextSection(
            translatedTextValue = TextFieldValue("")
        )
    }
}