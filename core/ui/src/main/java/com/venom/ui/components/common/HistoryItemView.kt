package com.venom.ui.components.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.data.model.OcrEntry
import com.venom.data.model.TranslationEntry
import com.venom.domain.model.IHistoryEntry
import com.venom.ui.components.bars.BookMarkActionBar
import com.venom.ui.components.bars.HistoryItemHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HistoryItemView(
    entry: IHistoryEntry,
    onEntryRemove: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Boolean, () -> Unit) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showCopiedToast by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                isExpanded = !isExpanded
                onItemClick()
            }, shape = MaterialTheme.shapes.medium, elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp, pressedElevation = 4.dp
        ), colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            HistoryItemHeader(
                timestamp = entry.timestamp,
                isBookmarked = entry.isBookmarked,
                onToggleBookmark = onToggleBookmark
            )

            content(isExpanded) { isExpanded = !isExpanded }

            BookMarkActionBar(
                onShareClick = onShareClick, onCopyClick = {
                    onCopyClick()
                    scope.launch {
                        showCopiedToast = true
                        delay(2000)
                        showCopiedToast = false
                    }
                }, onDeleteClick = onEntryRemove, showCopiedToast = showCopiedToast
            )
        }
    }
}

@Preview
@Composable
fun HistoryItemViewPreview() {
    HistoryItemView(entry = TranslationEntry(
        sourceText = "Hello, world!",
        translatedText = " , !",
        sourceLangName = "English",
        targetLangName = "Russian",
        sourceLangCode = "en",
        targetLangCode = "ru",
    ),
        onEntryRemove = {},
        onToggleBookmark = {},
        onShareClick = {},
        onCopyClick = {},
        onItemClick = {}) { _, _ -> }
}
