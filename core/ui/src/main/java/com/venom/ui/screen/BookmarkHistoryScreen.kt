package com.venom.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.venom.ui.components.inputs.CustomSearchBar
import com.venom.ui.components.lists.OcrBookmarkList
import com.venom.ui.components.lists.TransBookmarkList
import com.venom.ui.components.sections.CustomTabs
import com.venom.ui.components.sections.TabItem
import com.venom.ui.viewmodel.BookmarkOcrViewModel
import com.venom.ui.viewmodel.BookmarkViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkHistoryScreen(
    contentType: ContentType,
    translationViewModel: BookmarkViewModel = hiltViewModel(),
    ocrViewModel: BookmarkOcrViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTranslationItemClick: ((TranslationEntry) -> Unit) = {},
    onOcrItemClick: ((OcrEntry) -> Unit) = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showClearConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // View resources based on content type and tab
    val viewResources = when {
        selectedTab == 0 -> ViewResources(
            title = stringResource(R.string.bookmarks_title),
            stateIcon = R.drawable.icon_bookmark_outline,
            stateTitle = stringResource(R.string.bookmarks_empty_title),
            stateSubtitle = stringResource(R.string.bookmarks_empty_subtitle)
        )

        contentType == ContentType.TRANSLATION -> ViewResources(
            title = stringResource(R.string.history_title),
            stateIcon = R.drawable.icon_history,
            stateTitle = stringResource(R.string.history_empty_title),
            stateSubtitle = stringResource(R.string.history_empty_subtitle)
        )

        else -> ViewResources(
            title = stringResource(R.string.history_title),
            stateIcon = R.drawable.icon_camera,
            stateTitle = stringResource(R.string.history_empty_title),
            stateSubtitle = stringResource(R.string.history_empty_subtitle)
        )
    }

    // Fetch items based on selected tab
    LaunchedEffect(selectedTab) {
        val viewType = if (selectedTab == 0) ViewType.HISTORY else ViewType.BOOKMARKS
        translationViewModel.setViewType(viewType)
        ocrViewModel.setViewType(viewType)
        when (contentType) {
            ContentType.TRANSLATION -> translationViewModel.fetchItems(viewType)
            ContentType.OCR -> ocrViewModel.fetchItems(viewType)
            else -> {}
        }
    }

    if (showClearConfirmation) {
        ConfirmationDialog(title = stringResource(R.string.dialog_clear_all_title),
            message = stringResource(R.string.dialog_clear_all_message),
            onConfirm = {
                when (contentType) {
                    ContentType.TRANSLATION -> translationViewModel.clearAllItems()
                    ContentType.OCR -> ocrViewModel.clearAllItems()
                    else -> {}
                }
                showClearConfirmation = false
            },
            onDismiss = { showClearConfirmation = false })
    }

    Surface(
        color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TopBar(title = viewResources.title,
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
            CustomTabs(
                tabs = listOf(
                    TabItem(R.string.history_title, R.drawable.icon_history),
                    TabItem(R.string.bookmarks_title, R.drawable.icon_bookmark_outline),
                ),
                selectedTab = selectedTab,
                onTabSelected = { newTab -> selectedTab = newTab },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            CustomSearchBar(
                modifier = Modifier.padding(horizontal = 18.dp),
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it },
            )

            when (contentType) {
                ContentType.TRANSLATION -> {
                    val items by translationViewModel.bookmarkState.collectAsState()
                    TransBookmarkList(
                        items = items.items,
                        searchQuery = searchQuery,
                        onItemRemove = translationViewModel::removeItem,
                        onToggleBookmark = translationViewModel::toggleBookmark,
                        onShareClick = { context.shareText(it.translatedText) },
                        onCopyClick = { context.copyToClipboard(it.translatedText) },
                        onItemClick = onTranslationItemClick
                    )
                }

                ContentType.OCR -> {
                    val items by ocrViewModel.ocrBookmarkState.collectAsState()
                    OcrBookmarkList(
                        items = items.items,
                        searchQuery = searchQuery,
                        onItemRemove = ocrViewModel::removeItem,
                        onToggleBookmark = ocrViewModel::toggleBookmark,
                        onShareClick = { context.shareText(it.recognizedText) },
                        onCopyClick = { context.copyToClipboard(it.recognizedText) },
                        onItemClick = onOcrItemClick
                    )
                }

                else -> {}
            }
        }
    }
}

enum class ContentType {
    TRANSLATION, OCR, PHRASEBOOK, DIALOG, STACKCARD
}

enum class ViewType {
    BOOKMARKS, HISTORY
}