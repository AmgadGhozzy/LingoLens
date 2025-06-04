package com.venom.ui.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.data.model.TranslationEntry
import com.venom.ui.screen.history.TransHistoryItemView

@Composable
fun TransBookmarkList(
    items: List<TranslationEntry>,
    searchQuery: String,
    onItemRemove: (TranslationEntry) -> Unit,
    onToggleBookmark: (TranslationEntry) -> Unit,
    onShareClick: (TranslationEntry) -> Unit,
    onCopyClick: (TranslationEntry) -> Unit,
    onItemClick: ((TranslationEntry) -> Unit)
) {
    val filteredItems = items.filter { item ->
        searchQuery.isEmpty() || listOf(
            item.sourceText, item.translatedText, item.sourceLangCode, item.targetLangCode
        ).any { it.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredItems) { entry ->
            TransHistoryItemView(
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