package com.venom.lingopro.ui.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.venom.resources.R
import com.venom.ui.components.inputs.AnimatedResponseTextField

@Composable
fun TranslatedTextSection(
    translatedTextValue: TextFieldValue,
    modifier: Modifier = Modifier
) {
    AnimatedResponseTextField(
        text = translatedTextValue.text,
        placeHolderText = stringResource(R.string.translate_result_placeholder),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    )
}

@Preview(showBackground = true)
@Composable
private fun TranslatedTextSectionPreview() {
    MaterialTheme {
        TranslatedTextSection(translatedTextValue = TextFieldValue(""))
    }
}