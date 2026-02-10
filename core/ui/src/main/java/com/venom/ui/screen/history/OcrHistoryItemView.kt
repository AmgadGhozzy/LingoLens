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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import com.venom.data.local.entity.OcrEntity
import com.venom.ui.components.common.adp
import com.venom.utils.Extensions.toBitmap

@Composable
fun OcrHistoryItemView(
    entry: OcrEntity,
    onEntryRemove: (OcrEntity) -> Unit,
    onToggleBookmark: (OcrEntity) -> Unit,
    onShareClick: (OcrEntity) -> Unit,
    onCopyClick: (OcrEntity) -> Unit,
    onOpenInNew: (OcrEntity) -> Unit,
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
        onOpenInNew = { onOpenInNew(entry) },
        isExpanded = isExpanded,
        onExpandChange = onExpandChange,
        modifier = modifier
    ) { itemExpanded, _ ->
        OcrContent(entry, itemExpanded)
    }
}

@Composable
private fun OcrContent(entry: OcrEntity, isExpanded: Boolean) {
    val bitmap = remember(entry.imageData) { entry.imageData.toBitmap() }
    val shape = RoundedCornerShape(20.adp)

    Column {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(16.adp),
            tonalElevation = 2.adp
        ) {
            SelectionContainer {
                Text(
                    text = entry.recognizedText,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.adp)
                )
            }
        }
        Spacer(Modifier.height(16.adp))
        Surface(shape = shape, tonalElevation = 6.adp, shadowElevation = 8.adp) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.adp, max = if (isExpanded) 320.adp else 160.adp)
                    .clip(shape),
                contentScale = ContentScale.Crop
            )
        }
    }
}