package com.venom.ui.screen.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.venom.data.model.OcrEntry
import com.venom.utils.Extensions.toBitmap

@Composable
fun OcrHistoryItemView(
    entry: OcrEntry,
    onEntryRemove: (OcrEntry) -> Unit,
    onToggleBookmark: (OcrEntry) -> Unit,
    onShareClick: (OcrEntry) -> Unit,
    onCopyClick: (OcrEntry) -> Unit,
    onItemClick: (OcrEntry) -> Unit,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    HistoryItemView(
        entry = entry,
        onEntryRemove = { onEntryRemove(entry) },
        onToggleBookmark = { onToggleBookmark(entry) },
        onShareClick = { onShareClick(entry) },
        onCopyClick = { onCopyClick(entry) },
        onItemClick = { onItemClick(entry) },
        isExpanded = isExpanded,
        onExpandChange = onExpandChange,
        modifier = modifier
    ) { itemExpanded, _ ->
        OcrContent(
            entry = entry,
            isExpanded = itemExpanded
        )
    }
}

@Composable
private fun OcrContent(
    entry: OcrEntry,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            SelectionContainer {
                Text(
                    text = entry.recognizedText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Image(
                bitmap = entry.imageData.toBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 120.dp,
                        max = if (isExpanded) 320.dp else 160.dp
                    )
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}