package com.venom.ui.components.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.venom.data.model.OcrEntry
import com.venom.ui.components.common.HistoryItemView
import com.venom.utils.Extensions.toBitmap

@Composable
fun OcrHistoryItemView(
    entry: OcrEntry,
    onEntryRemove: (OcrEntry) -> Unit,
    onToggleBookmark: (OcrEntry) -> Unit,
    onShareClick: (OcrEntry) -> Unit,
    onCopyClick: (OcrEntry) -> Unit,
    onItemClick: (OcrEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    HistoryItemView(entry = entry,
        onEntryRemove = { onEntryRemove(entry) },
        onToggleBookmark = { onToggleBookmark(entry) },
        onShareClick = { onShareClick(entry) },
        onCopyClick = { onCopyClick(entry) },
        onItemClick = { onItemClick(entry) }) { isExpanded, onExpandClick ->
        OcrContent(
            entry = entry, isExpanded = isExpanded, modifier = modifier
        )
    }
}

@Composable
private fun OcrContent(
    entry: OcrEntry, isExpanded: Boolean, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = entry.recognizedText,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Image(
            bitmap = entry.imageData.toBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 256.dp else 120.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
    }
}

