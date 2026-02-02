package com.venom.lingopro.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.venom.resources.R
import com.venom.ui.components.common.adp
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
            modifier = modifier.fillMaxWidth().padding(vertical = 8.adp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TranslatedTextSectionPreview() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.adp)
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