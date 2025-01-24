package com.venom.ui.components.bars

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.venom.resources.R
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.BaseActionBar
import com.venom.utils.Extensions.formatTimestamp

@Composable
fun HistoryItemHeader(
    timestamp: Long,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseActionBar(
        leftAction = ActionItem.TextAction(text = timestamp.formatTimestamp(),
            onClick = {}), actions = listOf(
            ActionItem.Action(
                icon = if (isBookmarked) R.drawable.icon_bookmark_filled else R.drawable.icon_bookmark_outline,
                description = if (isBookmarked) R.string.bookmark_remove else R.string.bookmark_add,
                onClick = onToggleBookmark
            )
        ),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
    )
}

@Preview
@Composable
fun HistoryItemHeaderPreview() {
    HistoryItemHeader(timestamp = System.currentTimeMillis(),
        isBookmarked = false,
        onToggleBookmark = {})
}
