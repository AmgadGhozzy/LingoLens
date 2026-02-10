package com.venom.ui.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.venom.data.local.entity.OcrEntity
import com.venom.ui.components.common.adp

@Composable
fun OcrBookmarkList(
    items: List<OcrEntity>,
    searchQuery: String,
    onItemRemove: (OcrEntity) -> Unit,
    onToggleBookmark: (OcrEntity) -> Unit,
    onShareClick: (OcrEntity) -> Unit,
    onCopyClick: (OcrEntity) -> Unit,
    onOpenInNew: (OcrEntity) -> Unit
) {
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isEmpty()) items
        else items.filter { it.recognizedText.contains(searchQuery, true) }
    }

    var expandedItemId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.adp)
    ) {
        items(filteredItems, key = { it.id }) { entry ->
            OcrHistoryItemView(
                entry = entry,
                onEntryRemove = onItemRemove,
                onToggleBookmark = onToggleBookmark,
                onShareClick = onShareClick,
                onCopyClick = onCopyClick,
                onOpenInNew = onOpenInNew,
                isExpanded = expandedItemId == entry.id.toString(),
                onExpandChange = { expandedItemId = if (it) entry.id.toString() else null }
            )
        }
    }
}
