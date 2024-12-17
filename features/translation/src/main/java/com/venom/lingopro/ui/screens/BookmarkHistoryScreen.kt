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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ConfirmationDialog
import com.venom.lingopro.R
import com.venom.lingopro.data.model.TranslationEntry
import com.venom.lingopro.domain.model.ViewResources
import com.venom.lingopro.ui.components.bars.TopBar
import com.venom.lingopro.ui.components.buttons.CustomIcon
import com.venom.lingopro.ui.components.common.EmptyState
import com.venom.lingopro.ui.components.inputs.SearchBar
import com.venom.lingopro.ui.components.items.BookmarkHistoryItemView
import com.venom.lingopro.ui.viewmodel.BookmarkViewModel
import com.venom.lingopro.ui.viewmodel.TTSViewModel
import com.venom.lingopro.ui.viewmodel.ViewType
import com.venom.lingopro.utils.Extensions.copyToClipboard
import com.venom.lingopro.utils.Extensions.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkHistoryScreen(
    viewModel: BookmarkViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    viewType: ViewType,
    onBackClick: () -> Unit,
) {

    // Fetch items when the screen is first loaded or view type changes
    LaunchedEffect(viewType) {
        viewModel.fetchItems(viewType)
    }

    val state by viewModel.bookmarkState.collectAsState()
    val items = state.items

    var searchQuery by remember { mutableStateOf("") }
    var showClearConfirmation by remember { mutableStateOf(false) }
    var selectedItemToRemove by remember { mutableStateOf<TranslationEntry?>(null) }

    val context = LocalContext.current

    // Actions for copying, sharing, and speaking
    val copyAction: (String) -> Unit = { text -> context.copyToClipboard(text) }
    val shareAction: (String) -> Unit = { text -> (context as Activity).shareText(text) }
    val speakAction: (String) -> Unit = { text -> ttsViewModel.speak(text) }

    val viewResources = when (viewType) {
        ViewType.BOOKMARKS -> ViewResources(
            title = stringResource(R.string.bookmarks_title),
            stateIcon = R.drawable.icon_bookmark_outline,
            stateTitle = stringResource(R.string.bookmarks_empty_title),
            stateSubtitle = stringResource(R.string.bookmarks_empty_subtitle)
        )

        ViewType.HISTORY -> ViewResources(
            title = stringResource(R.string.history_title),
            stateIcon = R.drawable.icon_history,
            stateTitle = stringResource(R.string.history_empty_title),
            stateSubtitle = stringResource(R.string.history_empty_subtitle)
        )
    }


    // Handle clear all items confirmation
    if (showClearConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.dialog_clear_all_title),
            message = stringResource(R.string.dialog_clear_all_message),
            onConfirm = {
                viewModel.clearAllItems()
                showClearConfirmation = false
            },
            onDismiss = { showClearConfirmation = false },
        )
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
            // Top Bar
            BookmarkHistoryTopBar(
                title = viewResources.title,
                currentViewType = viewType,  // Pass current view type
                onBackClick = onBackClick,
                viewResources = viewResources,
                onClearAction = { showClearConfirmation = true },
                onToggleViewType = { viewModel.toggleViewType() }
            )

            // Search Bar
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Filtered Items
            val filteredItems = items.filter {
                searchQuery.isEmpty() || listOf(
                    it.sourceText, it.translatedText, it.sourceLang, it.targetLang
                ).any { text -> text.contains(searchQuery, ignoreCase = true) }
            }

            if (filteredItems.isNotEmpty()) {
                BookmarkHistoryList(items = filteredItems, onItemRemove = { item ->
                    selectedItemToRemove = item
                    viewModel.removeItem(item)
                }, onShareClick = { item ->
                    shareAction(item.translatedText)
                }, onCopyClick = { item ->
                    copyAction(item.translatedText)
                })
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

@Composable
private fun BookmarkHistoryTopBar(
    title: String,
    viewResources: ViewResources,
    currentViewType: ViewType,
    onBackClick: () -> Unit,
    onClearAction: () -> Unit,
    onToggleViewType: () -> Unit
) {
    TopBar(
        title = title,
        onNavigationClick = onBackClick,
        actions = {
            CustomIcon(
                icon = Icons.Rounded.Delete,
                contentDescription = stringResource(R.string.action_clear_history),
                onClick = onClearAction
            )
            CustomIcon(
                icon = viewResources.stateIcon,
                contentDescription = viewResources.stateTitle,
                onClick = onToggleViewType
            )
        }
    )
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
