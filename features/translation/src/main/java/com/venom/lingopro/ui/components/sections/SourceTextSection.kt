package com.venom.lingopro.ui.components.sections

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.PulseAnimation
import com.venom.ui.components.inputs.CustomTextField

/**
 * SourceTextSection is a composable function that displays a text input field that can be used
 * to enter text to be translated. It also displays a loading indicator if a translation is in
 * progress and a clear button to clear the input text.
 *
 * @param sourceTextValue the current text in the input field
 * @param onTextChange a callback that is called when the text in the input field changes
 * @param onClearText a callback that is called when the clear button is clicked
 * @param isLoading a boolean that indicates if a translation is in progress
 * @param modifier a modifier that can be used to customize the appearance of the component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceTextSection(
    sourceTextValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onClearText: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        CustomTextField(
            textValue = sourceTextValue,
            onTextChange = onTextChange,
            placeHolderText = stringResource(R.string.translate_type_text),
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        )

        if (sourceTextValue.text.isNotEmpty()) {
            Box(modifier = Modifier.size(48.dp)) {
                if (isLoading) PulseAnimation(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
                androidx.compose.animation.AnimatedVisibility(
                    visible = sourceTextValue.text.isNotEmpty(),
                    modifier = Modifier.align(Alignment.Center),
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(300))
                ) {
                    CustomButton(
                        icon = R.drawable.icon_clear,
                        contentDescription = stringResource(R.string.action_clear),
                        onClick = onClearText,
                        iconSize = 18.dp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SourceTextSectionPreview() {
    SourceTextSection(
        sourceTextValue = TextFieldValue("Example Text"),
        onTextChange = {},
        onClearText = {},
        isLoading = true
    )
}