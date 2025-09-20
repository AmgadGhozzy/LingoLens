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
import androidx.compose.ui.unit.dp
import com.venom.domain.model.TranslationResult

@Composable
fun TransBookmarkList(
    items: List<TranslationResult>,
    searchQuery: String,
    onItemRemove: (TranslationResult) -> Unit,
    onToggleBookmark: (TranslationResult) -> Unit,
    onShareClick: (TranslationResult) -> Unit,
    onCopyClick: (TranslationResult) -> Unit,
    onItemClick: (TranslationResult) -> Unit,
    onOpenInNew: (TranslationResult) -> Unit
) {
    val filteredItems = items.filter { item ->
        searchQuery.isEmpty() || listOf(
            item.sourceText, item.translatedText, item.sourceLang, item.targetLang
        ).any { it.contains(searchQuery, ignoreCase = true) }
    }

    var expandedItemId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredItems) { entry ->
            TransHistoryItemView(
                entry = entry,
                onEntryRemove = onItemRemove,
                onToggleBookmark = onToggleBookmark,
                onShareClick = onShareClick,
                onCopyClick = onCopyClick,
                onItemClick = { onItemClick(entry) },
                onOpenInNew = { onOpenInNew(entry) },
                isExpanded = expandedItemId == entry.id.toString(),
                onExpandChange = { isExpanding ->
                    expandedItemId = if (isExpanding) entry.id.toString() else null
                }
            )
        }
    }
}