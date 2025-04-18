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
 * @param onBookmark Callback for save action
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
    modifier: Modifier = Modifier,
    onBookmark: (() -> Unit)? = null,
    isSaved: Boolean,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onFullscreen: (() -> Unit)? = null,
    onSpeak: () -> Unit,
    isSpeaking: Boolean
) {
    val leftAction = ActionItem.Action(
        icon = if (isSpeaking) R.drawable.icon_record else R.drawable.icon_sound,
        description = R.string.action_speak_source,
        onClick = onSpeak
    )

    val actions = listOfNotNull(
        onFullscreen?.let {
            ActionItem.Action(
                icon = R.drawable.icon_fullscreen,
                description = R.string.action_fullscreen_source,
                onClick = it
            )
        },
        ActionItem.Action(
            icon = R.drawable.icon_share,
            description = R.string.action_share,
            onClick = onShare
        ),
        ActionItem.Action(
            icon = R.drawable.icon_copy,
            description = R.string.action_copy_source,
            onClick = onCopy
        ),
        onBookmark?.let {
            ActionItem.Action(
                icon = if (isSaved) R.drawable.icon_bookmark_filled else R.drawable.icon_bookmark_outline,
                description = R.string.action_save,
                onClick = it
            )
        }
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
        onBookmark = { },
        onShare = {},
    )
}
