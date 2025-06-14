package com.venom.lingopro.ui.components.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.inputs.AnimatedResponseTextField
import com.venom.ui.components.other.TextShimmer

@Composable
fun TranslatedTextSection(
    translatedTextValue: TextFieldValue,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (isLoading && translatedTextValue.text.isEmpty()) {
        TextShimmer()
    } else {
        AnimatedResponseTextField(
            text = translatedTextValue.text,
            placeHolderText = stringResource(R.string.translate_result_placeholder),
            modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TranslatedTextSectionPreview() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Loading state preview
            TranslatedTextSection(
                translatedTextValue = TextFieldValue(""),
                isLoading = true
            )

            // Content state preview
            TranslatedTextSection(
                translatedTextValue = TextFieldValue("Translation result appears here"),
                isLoading = false
            )
        }
    }
}