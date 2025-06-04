package com.venom.ui.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.data.model.OcrEntry
import com.venom.ui.screen.history.OcrHistoryItemView

@Composable
fun OcrBookmarkList(
    items: List<OcrEntry>,
    searchQuery: String,
    onItemRemove: (OcrEntry) -> Unit,
    onToggleBookmark: (OcrEntry) -> Unit,
    onShareClick: (OcrEntry) -> Unit,
    onCopyClick: (OcrEntry) -> Unit,
    onItemClick: ((OcrEntry) -> Unit)
) {
    val filteredItems = items.filter { item ->
        searchQuery.isEmpty() || item.recognizedText.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredItems) { entry ->
            OcrHistoryItemView(
                entry = entry,
                onEntryRemove = onItemRemove,
                onToggleBookmark = onToggleBookmark,
                onShareClick = onShareClick,
                onCopyClick = onCopyClick,
                onItemClick = onItemClick
            )
        }
    }
}
