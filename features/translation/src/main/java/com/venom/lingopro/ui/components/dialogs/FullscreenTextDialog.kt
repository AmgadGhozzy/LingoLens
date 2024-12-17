package com.venom.lingopro.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.venom.lingopro.ui.components.bars.TranslatedTextActionBar
import com.venom.lingopro.ui.components.buttons.CustomIcon
import com.venom.lingopro.ui.components.inputs.CustomTextField
import com.venom.lingopro.ui.theme.LingoProTheme
import com.venom.lingopro.utils.Extensions.getSelectedOrFullText

/**
 * A fullscreen dialog for displaying text with additional actions
 *
 * @param text The text to be displayed in the dialog
 * @param onDismiss Callback triggered when the dialog is dismissed
 * @param onCopy Callback for copying the text
 * @param onShare Callback for sharing the text
 * @param onSpeak Callback for speaking the text
 * @param visible Whether the dialog is currently visible
 * @param properties Optional dialog properties
 */
@Composable
fun FullscreenTextDialog(
    textValue: TextFieldValue,
    onDismiss: () -> Unit,
    onCopy: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    onSpeak: (String) -> Unit = {},
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true, dismissOnClickOutside = false, usePlatformDefaultWidth = false
    )
) {
    CustomCard {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Dismiss Button
            CustomIcon(
                icon = Icons.Rounded.Close,
                contentDescription = "Close",
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )

            // Scrollable Text Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
                    .verticalScroll(rememberScrollState()), contentAlignment = Alignment.Center
            ) {
                CustomTextField(
                    textValue = textValue,
                )
            }

            // Action Bar at the Bottom
            TranslatedTextActionBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                onSave = { },
                onCopy = { onCopy(textValue.getSelectedOrFullText()) },
                onShare = { onShare(textValue.getSelectedOrFullText()) },
                onSpeak = { onSpeak(textValue.getSelectedOrFullText()) },
                onFullscreen = {}, // No-op since it's already fullscreen
            )
        }
    }
}

// Preview function
@Composable
fun FullscreenTextDialogPreview() {
    LingoProTheme {
        FullscreenTextDialog(
            textValue = TextFieldValue("Hello"),
            onDismiss = { },
            onCopy = { },
            onShare = { },
            onSpeak = { },
        )
    }
}