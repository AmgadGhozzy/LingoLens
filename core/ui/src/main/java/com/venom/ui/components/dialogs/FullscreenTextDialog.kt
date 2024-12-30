package com.venom.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.resources.R
import com.venom.ui.components.bars.TranslatedTextActionBar
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.inputs.CustomTextField
import com.venom.ui.theme.LingoLensTheme
import com.venom.utils.Extensions.getSelectedOrFullText

/**
 * A fullscreen dialog for displaying text with additional actions
 *
 * @param textValue The text to be displayed in the dialog
 * @param onDismiss Callback triggered when the dialog is dismissed
 * @param onCopy Callback for copying the text
 * @param onShare Callback for sharing the text
 * @param onSpeak Callback for speaking the text
 * @param properties Optional dialog properties
 */
@Composable
fun FullscreenTextDialog(
    textValue: TextFieldValue,
    onDismiss: () -> Unit,
    onCopy: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    onSpeak: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            dismissOnBackPress = true, usePlatformDefaultWidth = false
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)

        ) {
            CustomButton(
                icon = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.action_close),
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            )

            CustomTextField(
                textValue = textValue,
                minFontSize = 24,
                maxFontSize = 32,
                maxLines = 50,
                minHeight = 128.dp,
                maxHeight = 256.dp,
                isReadOnly = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )

            CustomCard(modifier = Modifier.padding(bottom = 32.dp)) {
                TranslatedTextActionBar(onSave = {},
                    onCopy = { onCopy(textValue.getSelectedOrFullText()) },
                    onShare = { onShare(textValue.getSelectedOrFullText()) },
                    onSpeak = { onSpeak(textValue.getSelectedOrFullText()) },
                    onFullscreen = {})
            }
        }
    }
}

@Preview
@Composable
fun FullscreenTextDialogPreview() {
    LingoLensTheme {
        FullscreenTextDialog(
            textValue = TextFieldValue("Hello"),
            onDismiss = { },
            onCopy = { },
            onShare = { },
            onSpeak = { },
        )
    }
}