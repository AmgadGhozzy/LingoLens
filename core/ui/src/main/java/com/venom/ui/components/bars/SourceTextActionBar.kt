package com.venom.ui.components.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton

/**
 * Bottom action bar with multiple functional icons.
 *
 * @param onSpeak Callback for speak/sound action.
 * @param onOcr Callback for OCR (optical character recognition) action.
 * @param onPaste Callback for paste action.
 * @param onCopy Callback for copy action.
 * @param onFullscreen Callback for fullscreen action.
 * @param onSpeechToText Callback for speech-to-text action.
 * @param modifier Optional modifier for the entire row.
 */
@Composable
fun SourceTextActionBar(
    onSpeak: () -> Unit,
    onOcr: () -> Unit,
    onPaste: () -> Unit,
    onCopy: () -> Unit,
    onFullscreen: () -> Unit,
    onSpeechToText: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Speak Button
        CustomButton(
            icon = R.drawable.icon_sound,
            contentDescription = stringResource(R.string.action_speak),
            onClick = onSpeak
        )

        // Additional Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomButton(
                icon = R.drawable.icon_fullscreen,
                contentDescription = stringResource(R.string.action_fullscreen),
                onClick = onFullscreen
            )

            // Copy Button
            CustomButton(
                icon = R.drawable.icon_copy,
                contentDescription = stringResource(R.string.action_copy),
                onClick = onCopy
            )

            // Paste Button
            CustomButton(
                icon = R.drawable.icon_paste,
                contentDescription = stringResource(R.string.action_paste),
                onClick =
                onPaste
            )

            // OCR Button
            CustomButton(
                icon = R.drawable.icon_camera, contentDescription = "OCR", onClick = onOcr
            )

            // Speech to Text Button
            CustomButton(
                icon = R.drawable.icon_mic,
                contentDescription = stringResource(R.string.action_speech_to_text),
                onClick = onSpeechToText
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SourceActionBarPreview() {
    SourceTextActionBar(
        onSpeak = { },
        onFullscreen = { },
        onCopy = { },
        onPaste = { },
        onOcr = { },
        onSpeechToText = { }
    )
}
