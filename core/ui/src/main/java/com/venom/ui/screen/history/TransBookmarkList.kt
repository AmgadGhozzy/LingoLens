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
import com.venom.domain.model.TranslationResult
import com.venom.ui.components.common.adp

@Composable
fun TransBookmarkList(
    items: List<TranslationResult>,
    searchQuery: String,
    onItemRemove: (TranslationResult) -> Unit,
    onToggleBookmark: (TranslationResult) -> Unit,
    onShareClick: (TranslationResult) -> Unit,
    onCopyClick: (TranslationResult) -> Unit,
    onOpenInNew: (TranslationResult) -> Unit
) {
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isEmpty()) items
        else items.filter { item ->
            listOf(item.sourceText, item.translatedText, item.sourceLang, item.targetLang)
                .any { it.contains(searchQuery, true) }
        }
    }

    var expandedItemId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.adp)
    ) {
        items(filteredItems, key = { it.id }) { entry ->
            TransHistoryItemView(
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
