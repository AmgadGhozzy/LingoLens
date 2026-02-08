package com.venom.ui.components.bars

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.venom.resources.R
import com.venom.ui.components.buttons.STTButton
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.BaseActionBar

/**
 * Action bar for source text with STT button and common actions.
 * Follows Material Design 3 guidelines.
 */
@Composable
fun SourceTextActionBar(
    onOcr: () -> Unit,
    onPaste: () -> Unit,
    onFullscreen: () -> Unit,
    onSpeechToTextStart: () -> Unit,
    onSpeechToTextEnd: () -> Unit,
    isSpeechToTextActive: Boolean = false,
    onSentenceExplorer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BaseActionBar(
        leftAction = ActionItem.Action(
            R.drawable.icon_fullscreen, R.string.action_fullscreen,
            onFullscreen
        ),
        actions = buildActionsList(onSentenceExplorer, onPaste, onOcr),
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

private fun buildActionsList(
    onSentenceExplorer: (() -> Unit)?,
    onPaste: () -> Unit,
    onOcr: () -> Unit
) = buildList {

    onSentenceExplorer?.let {
        add(ActionItem.Action(R.drawable.icon_quotes, R.string.sentence_explorer, it))
    }
    add(ActionItem.Action(R.drawable.icon_paste, R.string.action_paste, onPaste))
    add(ActionItem.Action(R.drawable.ic_camera, R.string.action_ocr, onOcr))
}