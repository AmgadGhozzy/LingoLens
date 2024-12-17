package com.venom.lingopro.ui.components.bars

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
import com.venom.lingopro.R
import com.venom.lingopro.ui.components.buttons.CustomIcon

/**
 * Bottom action bar with multiple functional icons
 *
 * @param onSave Callback for save action
 * @param onCopy Callback for copy action
 * @param onShare Callback for share action
 * @param onFullscreen Callback for fullscreen action
 * @param onSpeak Callback for speak/sound action
 * @param modifier Optional modifier for the entire row
 */
@Composable
fun TranslatedTextActionBar(
    onSave: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onFullscreen: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Speak Button
        CustomIcon(
            icon = R.drawable.icon_sound,
            contentDescription = stringResource(R.string.action_speak),
            onClick = onSpeak
        )

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fullscreen Button
            CustomIcon(
                icon = R.drawable.icon_fullscreen,
                contentDescription = stringResource(R.string.action_fullscreen),
                onClick = onFullscreen
            )

            // Share Button
            CustomIcon(
                icon = R.drawable.icon_share,
                contentDescription = stringResource(R.string.action_share),
                onClick =
                onShare
            )

            // Copy Button
            CustomIcon(
                icon = R.drawable.icon_copy,
                contentDescription = stringResource(R.string.action_copy),
                onClick = onCopy
            )

            // Save Button
            CustomIcon(
                icon = R.drawable.icon_bookmark_outline,
                contentDescription = stringResource(R.string.action_save),
                onClick = onSave
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TranslatedTextActionBarPreview() {
    TranslatedTextActionBar(onSpeak = { }, onFullscreen = { }, onCopy = { }, onSave = { }, onShare = {},)
}
