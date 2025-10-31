package com.venom.ui.components.bars

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.venom.resources.R
import com.venom.ui.components.buttons.STTButton
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.BaseActionBar

/**
 * Bottom action bar with multiple functional icons including WhatsApp-style STT button.
 *
 * @param onSpeak Callback for speak/sound action.
 * @param isSpeaking Whether the text is speaking.
 * @param onOcr Callback for OCR (optical character recognition) action.
 * @param onPaste Callback for paste action.
 * @param onCopy Callback for copy action.
 * @param onFullscreen Callback for fullscreen action.
 * @param onSpeechToTextStart Callback when STT button is pressed (start recording).
 * @param onSpeechToTextEnd Callback when STT button is released (stop recording).
 * @param isSpeechToTextActive Whether speech-to-text is currently active.
 * @param modifier Optional modifier for the entire row.
 */
@Composable
fun SourceTextActionBar(
    onSpeak: () -> Unit,
    isSpeaking: Boolean,
    onOcr: () -> Unit,
    onPaste: () -> Unit,
    onCopy: () -> Unit,
    onFullscreen: () -> Unit,
    onSpeechToTextStart: () -> Unit,
    onSpeechToTextEnd: () -> Unit,
    isSpeechToTextActive: Boolean = false,
    onSentenceExplorer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val leftAction = ActionItem.Action(
        icon = if (isSpeaking) R.drawable.icon_record else R.drawable.icon_sound,
        description = R.string.action_speak,
        onClick = onSpeak
    )

    val actions = buildList {
        add(
            ActionItem.Action(
                icon = R.drawable.icon_fullscreen,
                description = R.string.action_fullscreen,
                onClick = onFullscreen
            )
        )

        onSentenceExplorer?.let {
            add(
                ActionItem.Action(
                    icon = R.drawable.icon_quotes,
                    description = R.string.sentence_explorer,
                    onClick = it
                )
            )
        }

        add(
            ActionItem.Action(
                icon = R.drawable.icon_copy,
                description = R.string.action_copy,
                onClick = onCopy
            )
        )

        add(
            ActionItem.Action(
                icon = R.drawable.icon_paste,
                description = R.string.action_paste,
                onClick = onPaste
            )
        )

        add(
            ActionItem.Action(
                icon = R.drawable.icon_camera,
                description = R.string.action_ocr,
                onClick = onOcr
            )
        )
    }

    BaseActionBar(
        leftAction = leftAction,
        actions = actions,
        modifier = modifier,
        trailingContent = {
            STTButton(
                onPressStart = onSpeechToTextStart,
                onPressEnd = onSpeechToTextEnd,
                isActive = isSpeechToTextActive
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SourceActionBarPreview() {
    MaterialTheme {
        SourceTextActionBar(
            onSpeak = { },
            isSpeaking = false,
            onFullscreen = { },
            onCopy = { },
            onPaste = { },
            onOcr = { },
            onSpeechToTextStart = { },
            onSpeechToTextEnd = { },
            isSpeechToTextActive = false,
            onSentenceExplorer = { }
        )
    }
}