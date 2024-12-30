package com.venom.ui.components.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton

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
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var isSaved by remember { mutableStateOf(false) }
        // Speak Button
        CustomButton(
            icon = R.drawable.icon_sound,
            contentDescription = stringResource(R.string.action_speak),
            onClick = onSpeak
        )

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Fullscreen Button
            CustomButton(
                icon = R.drawable.icon_fullscreen,
                contentDescription = stringResource(R.string.action_fullscreen),
                onClick = onFullscreen
            )

            // Share Button
            CustomButton(
                icon = R.drawable.icon_share,
                contentDescription = stringResource(R.string.action_share),
                onClick = onShare
            )

            // Copy Button
            CustomButton(
                icon = R.drawable.icon_copy,
                contentDescription = stringResource(R.string.action_copy),
                onClick = onCopy
            )

            // Save Button
            CustomButton(icon = if (isSaved) R.drawable.icon_bookmark_filled else R.drawable.icon_bookmark_outline,
                contentDescription = stringResource(R.string.action_save),
                onClick = { onSave; isSaved = !isSaved })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TranslatedTextActionBarPreview() {
    TranslatedTextActionBar(
        onSpeak = { },
        onFullscreen = { },
        onCopy = { },
        onSave = { },
        onShare = {},
    )
}
