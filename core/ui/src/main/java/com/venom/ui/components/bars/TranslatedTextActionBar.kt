package com.venom.ui.components.bars

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.venom.resources.R
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.BaseActionBar

/**
 * Bottom action bar with multiple functional icons
 *
 * @param onSave Callback for save action
 * @param isSaved Whether the text is saved
 * @param onCopy Callback for copy action
 * @param onShare Callback for share action
 * @param onFullscreen Callback for fullscreen action
 * @param onSpeak Callback for speak/sound action
 * @param isSpeaking Whether the text is speaking
 * @param modifier Optional modifier for the entire row
 */

@Composable
fun TranslatedTextActionBar(
    onSave: () -> Unit,
    isSaved: Boolean,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onFullscreen: () -> Unit,
    onSpeak: () -> Unit,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val leftAction = ActionItem.Action(
        icon = if (isSpeaking) R.drawable.icon_record else R.drawable.icon_sound,
        textRes = R.string.action_speak,
        onClick = onSpeak
    )

    val actions = listOf(
        ActionItem.Action(
            icon = R.drawable.icon_fullscreen,
            textRes = R.string.action_fullscreen,
            onClick = onFullscreen
        ),
        ActionItem.Action(
            icon = R.drawable.icon_share,
            textRes = R.string.action_share,
            onClick = onShare
        ),
        ActionItem.Action(
            icon = R.drawable.icon_copy,
            textRes = R.string.action_copy,
            onClick = onCopy
        ),
        ActionItem.Action(
            icon = if (isSaved) R.drawable.icon_bookmark_filled else R.drawable.icon_bookmark_outline,
            textRes = R.string.action_save,
            onClick = onSave
        )
    )

    BaseActionBar(
        leftAction = leftAction,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        actions = actions,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun TranslatedTextActionBarPreview() {
    TranslatedTextActionBar(
        onSpeak = { },
        isSpeaking = false,
        isSaved = false,
        onFullscreen = { },
        onCopy = { },
        onSave = { },
        onShare = {},
    )
}
