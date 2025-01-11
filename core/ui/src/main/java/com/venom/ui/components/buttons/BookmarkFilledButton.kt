package com.venom.ui.components.buttons

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun BookmarkFilledButton(
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 48.dp,
    shape: Shape = IconButtonDefaults.filledShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val colors = IconButtonDefaults.filledIconButtonColors(
        containerColor = if (isBookmarked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primaryContainer.copy(
            alpha = 0.8f
        ),
        contentColor = if (isBookmarked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    )

    FilledIconButton(
        onClick = onToggleBookmark,
        modifier = modifier
            .scale(if (isBookmarked) 1.1f else 1f)
            .size(size),
        enabled = enabled,
        shape = shape,
        colors = colors,
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(
                if (isBookmarked) R.drawable.icon_bookmark_filled
                else R.drawable.icon_bookmark_outline
            ), contentDescription = stringResource(
                if (isBookmarked) R.string.bookmark_remove
                else R.string.bookmark_add
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkFilledButtonPreview() {
    MaterialTheme {
        BookmarkFilledButton(isBookmarked = false, onToggleBookmark = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkFilledButtonBookmarkedPreview() {
    MaterialTheme {
        BookmarkFilledButton(isBookmarked = true, onToggleBookmark = {})
    }
}
