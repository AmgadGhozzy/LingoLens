package com.venom.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.data.model.OcrEntry
import com.venom.data.model.TranslationEntry
import com.venom.domain.model.ViewResources
import com.venom.resources.R
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.dialogs.ConfirmationDialog
import com.venom.ui.components.inputs.SearchBar
import com.venom.ui.components.items.OcrHistoryItemView
import com.venom.ui.components.items.TransHistoryItemView
import com.venom.ui.components.sections.BookmarkHistoryTabs
import com.venom.ui.viewmodel.BookmarkOcrViewModel
import com.venom.ui.viewmodel.BookmarkViewModel
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkHistoryScreen(
    translationViewModel: BookmarkViewModel = hiltViewModel(),
    ocrViewModel: BookmarkOcrViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    contentType: ContentType,
    onBackClick: () -> Unit,
) {
    var contentType by remember { mutableStateOf(contentType) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showClearConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val copyAction: (String) -> Unit = { text -> context.copyToClipboard(text) }
    val shareAction: (String) -> Unit = { text -> context.shareText(text) }
    val speakAction: (String) -> Unit = { text -> ttsViewModel.speak(text) }

    // Collect states from both ViewModels
    val translationState by translationViewModel.bookmarkState.collectAsState()
    val ocrState by ocrViewModel.ocrBookmarkState.collectAsState()

    // Set view type based on selected tab
    LaunchedEffect(selectedTab) {
        val viewType = if (selectedTab == 0) ViewType.BOOKMARKS else ViewType.HISTORY
        if (contentType == ContentType.TRANSLATION) translationViewModel.fetchItems(viewType) else ocrViewModel.fetchItems(
            viewType
        )
    }

    val viewResources = when (contentType) {
        ContentType.TRANSLATION -> when (selectedTab) {
            0 -> ViewResources(
                title = stringResource(R.string.bookmarks_title),
                stateIcon = R.drawable.icon_bookmark_outline,
                stateTitle = stringResource(R.string.bookmarks_empty_title),
                stateSubtitle = stringResource(R.string.bookmarks_empty_subtitle)
            )

            else -> ViewResources(
                title = stringResource(R.string.history_title),
                stateIcon = R.drawable.icon_history,
                stateTitle = stringResource(R.string.history_empty_title),
                stateSubtitle = stringResource(R.string.history_empty_subtitle)
            )
        }

        ContentType.OCR -> when (selectedTab) {
            0 -> ViewResources(
                title = stringResource(R.string.bookmarks_title),
                stateIcon = R.drawable.icon_bookmark_outline,
                stateTitle = stringResource(R.string.bookmarks_empty_title),
                stateSubtitle = stringResource(R.string.bookmarks_empty_subtitle)
            )

            else -> ViewResources(
                title = stringResource(R.string.history_title),
                stateIcon = R.drawable.icon_camera,
                stateTitle = stringResource(R.string.history_empty_title),
                stateSubtitle = stringResource(R.string.history_empty_subtitle)
            )
        }
    }

    if (showClearConfirmation) {
        ConfirmationDialog(title = stringResource(R.string.dialog_clear_all_title),
            message = stringResource(R.string.dialog_clear_all_message),
            onConfirm = {
                when (contentType) {
                    ContentType.TRANSLATION -> translationViewModel.clearAllItems()
                    ContentType.OCR -> ocrViewModel.clearAllItems()
                }
                showClearConfirmation = false
            },
            onDismiss = { showClearConfirmation = false })
    }

    Surface(
        color = MaterialTheme.colorScheme.background, modifier = Modifier.Companion.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TopBar(
                title = viewResources.title,
                onLeadingIconClick = onBackClick,
                leadingIcon = R.drawable.icon_back,
                actions = {
                CustomButton(
                    icon = R.drawable.icon_delete,
                    onClick = { showClearConfirmation = true },
                    selected = true,
                    selectedTint = MaterialTheme.colorScheme.error,
                    contentDescription = stringResource(R.string.action_clear_history),
                )
            })

            // View Type Selector (Bookmarks/History)
            BookmarkHistoryTabs(
                selectedTab = selectedTab, onTabSelected = { newTab ->
                    selectedTab = newTab
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            SearchBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it },
            )

            when (contentType) {
                ContentType.TRANSLATION -> {
                    TranslationList(items = translationState.items,
                        searchQuery = searchQuery,
                        onItemRemove = { translationViewModel.removeItem(it) },
                        onToggleBookmark = { translationViewModel.toggleBookmark(it) },
                        onShareClick = { shareAction(it.translatedText) },
                        onCopyClick = { copyAction(it.translatedText) })
                }

                ContentType.OCR -> {
                    OcrList(items = ocrState.items,
                        searchQuery = searchQuery,
                        onItemRemove = { ocrViewModel.removeItem(it) },
                        onToggleBookmark = { ocrViewModel.toggleBookmark(it) },
                        onShareClick = { shareAction(it.recognizedText) },
                        onCopyClick = { copyAction(it.recognizedText) })
                }
            }
        }
    }
}

@Composable
private fun TranslationList(
    items: List<TranslationEntry>,
    searchQuery: String,
    onItemRemove: (TranslationEntry) -> Unit,
    onToggleBookmark: (TranslationEntry) -> Unit,
    onShareClick: (TranslationEntry) -> Unit,
    onCopyClick: (TranslationEntry) -> Unit
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
                onCopyClick = onCopyClick
            )
        }
    }
}

@Composable
private fun OcrList(
    items: List<OcrEntry>,
    searchQuery: String,
    onItemRemove: (OcrEntry) -> Unit,
    onToggleBookmark: (OcrEntry) -> Unit,
    onShareClick: (OcrEntry) -> Unit,
    onCopyClick: (OcrEntry) -> Unit
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
            OcrHistoryItemView(entry = entry,
                onEntryRemove = onItemRemove,
                onToggleBookmark = onToggleBookmark,
                onShareClick = onShareClick,
                onCopyClick = onCopyClick,
                onItemClick = {})
        }
    }
}

enum class ContentType {
    TRANSLATION, OCR
}

enum class ViewType {
    BOOKMARKS, HISTORY
}
