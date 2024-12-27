package com.venom.ui.components.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.venom.ui.components.buttons.BookmarkButton
import com.venom.utils.Extensions.formatTimestamp


@Composable
fun HistoryItemHeader(
    timestamp: Long,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timestamp.formatTimestamp(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        BookmarkButton(
            isBookmarked = isBookmarked, onToggleBookmark = onToggleBookmark
        )
    }
}

@Preview
@Composable
fun HistoryItemHeaderPreview() {
    HistoryItemHeader(
        timestamp = System.currentTimeMillis(),
        isBookmarked = false,
        onToggleBookmark = {}
    )
}