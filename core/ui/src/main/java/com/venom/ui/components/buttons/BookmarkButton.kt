package com.venom.ui.components.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.venom.resources.R


@Composable
fun BookmarkButton(
    isBookmarked: Boolean, onToggleBookmark: () -> Unit, modifier: Modifier = Modifier
) {
    CustomButton(
        icon = if (isBookmarked) R.drawable.icon_bookmark_filled
        else R.drawable.icon_bookmark_outline,
        onClick = onToggleBookmark,
        selected = isBookmarked,
        contentDescription = stringResource(
            if (isBookmarked) R.string.bookmark_remove
            else R.string.bookmark_add
        ),
        modifier = modifier
    )
}