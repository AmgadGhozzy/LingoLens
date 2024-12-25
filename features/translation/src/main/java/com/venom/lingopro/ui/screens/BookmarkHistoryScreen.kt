package com.venom.lingopro.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.data.model.TranslationEntry
import com.venom.domain.model.ViewResources
import com.venom.lingopro.ui.viewmodel.BookmarkViewModel
import com.venom.lingopro.ui.viewmodel.ViewType
import com.venom.resources.R
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.EmptyState
import com.venom.ui.components.dialogs.ConfirmationDialog
import com.venom.ui.components.inputs.SearchBar
import com.venom.ui.components.inputs.rememberSearchBarState
import com.venom.ui.components.items.BookmarkHistoryItemView
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkHistoryScreen(
    viewModel: BookmarkViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    viewType: ViewType,
    onBackClick: () -> Unit,
) {
    LaunchedEffect(viewType) {
        viewModel.fetchItems(viewType)
    }

    val state by viewModel.bookmarkState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val searchState = rememberSearchBarState(onSearchTriggered = { query ->
        // Handle search
    })
    var showClearConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val copyAction: (String) -> Unit = { text -> context.copyToClipboard(text) }
    val shareAction: (String) -> Unit = { text -> (context as Activity).shareText(text) }
    val speakAction: (String) -> Unit = { text -> ttsViewModel.speak(text) }

    val viewResources = when (selectedTab) {
        0 -> ViewResources(
            title = stringResource(R.string.bookmarks_title),
            stateIcon = R.drawable.icon_bookmark_outline,
            stateTitle = stringResource(R.string.bookmarks_empty_title),
            stateSubtitle = stringResource(R.string.bookmarks_empty_subtitle)
        )

        1 -> ViewResources(
            title = stringResource(R.string.history_title),
            stateIcon = R.drawable.icon_history,
            stateTitle = stringResource(R.string.history_empty_title),
            stateSubtitle = stringResource(R.string.history_empty_subtitle)
        )

        3 -> ViewResources(
            title = stringResource(R.string.ocr_title),
            stateIcon = R.drawable.icon_camera,
            stateTitle = stringResource(R.string.ocr_empty_title),
            stateSubtitle = stringResource(R.string.ocr_empty_subtitle)
        )

        else -> ViewResources(
            title = stringResource(R.string.bookmarks_title),
            stateIcon = R.drawable.icon_bookmark_outline,
            stateTitle = stringResource(R.string.bookmarks_empty_title),
            stateSubtitle = stringResource(R.string.bookmarks_empty_subtitle)
        )
    }

    if (showClearConfirmation) {
        ConfirmationDialog(title = stringResource(R.string.dialog_clear_all_title),
            message = stringResource(R.string.dialog_clear_all_message),
            onConfirm = {
                viewModel.clearAllItems()
                showClearConfirmation = false
            },
            onDismiss = { showClearConfirmation = false })
    }

    Surface(
        color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TopBar(title = viewResources.title, onNavigationClick = onBackClick, actions = {
                CustomButton(icon = Icons.Rounded.Delete,
                    contentDescription = stringResource(R.string.action_clear_history),
                    onClick = { showClearConfirmation = true })
            })

            TabRow(
                selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()
            ) {
                Tab(selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.bookmarks_title)) })
                Tab(selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.history_title)) })
                Tab(selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(stringResource(R.string.ocr_title)) })
                Tab(selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text(stringResource(R.string.ocr_title)) })
            }

            SearchBar(
                state = searchState, modifier = Modifier.padding(vertical = 8.dp)
            )

            val filteredItems = state.items.filter { item ->
                val matchesTab = when (selectedTab) {
                    0 -> item.isBookmarked
                    1 -> !item.isBookmarked
                    else -> !item.isBookmarked
                }

                matchesTab && (searchState.searchQuery.isEmpty() || listOf(
                    item.sourceText, item.translatedText, item.sourceLang, item.targetLang
                ).any { it.contains(searchState.searchQuery, ignoreCase = true) })
            }

            if (filteredItems.isNotEmpty()) {
                BookmarkHistoryList(items = filteredItems,
                    onItemRemove = { viewModel.removeItem(it) },
                    onShareClick = { shareAction(it.translatedText) },
                    onCopyClick = { copyAction(it.translatedText) })
            } else {
                EmptyState(
                    icon = viewResources.stateIcon,
                    title = viewResources.stateTitle,
                    subtitle = viewResources.stateSubtitle
                )
            }
        }
    }
}

enum class EntryType {
    TRANSLATION, OCR
}

@Composable
private fun BookmarkHistoryList(
    items: List<TranslationEntry>,
    onItemRemove: (TranslationEntry) -> Unit,
    onShareClick: (TranslationEntry) -> Unit,
    onCopyClick: (TranslationEntry) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { entry ->
            BookmarkHistoryItemView(
                bookmark = entry,
                onBookmarkRemove = onItemRemove,
                onShareClick = onShareClick,
                onCopyClick = onCopyClick
            )
        }
    }
}

@Preview
@Composable
fun BookmarksScreenPreview() {
    BookmarkHistoryScreen(
        onBackClick = {},
        viewType = ViewType.BOOKMARKS,
    )
}
