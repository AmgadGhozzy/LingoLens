package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.BookmarkFilledButton
import com.venom.ui.components.buttons.CustomFilledIconButton

@Composable
fun ExpandCardActions(
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onSpeakClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        CustomFilledIconButton(
            icon = R.drawable.icon_sound,
            contentDescription = stringResource(id = R.string.action_speak),
            onClick = onSpeakClick,
            size = 48.dp,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        BookmarkFilledButton(
            isBookmarked = isBookmarked, onToggleBookmark = onBookmarkClick, size = 48.dp
        )
    }
}
