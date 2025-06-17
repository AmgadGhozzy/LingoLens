package com.venom.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.dialogs.ConfirmationDialog
import com.venom.ui.components.inputs.CustomSearchBar
import com.venom.ui.components.lists.*
import com.venom.ui.components.sections.*
import com.venom.ui.viewmodel.*
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkHistoryScreen(
    contentType: ContentType,
    translationViewModel: BookmarkViewModel = hiltViewModel(),
    ocrViewModel: BookmarkOcrViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTranslationItemClick: (TranslationEntry) -> Unit = {},
    onOcrItemClick: (OcrEntry) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val viewResources = if (selectedTab == 0) {
        ViewResources(
            title = stringResource(R.string.history_title),
            stateIcon = R.drawable.icon_history,
            stateTitle = stringResource(R.string.history_empty_title),
            stateSubtitle = stringResource(R.string.history_empty_subtitle)
        )
    } else {
        ViewResources(
            title = stringResource(R.string.bookmarks_title),
            stateIcon = R.drawable.icon_bookmark_outline,
            stateTitle = stringResource(R.string.bookmarks_empty_title),
            stateSubtitle = stringResource(R.string.bookmarks_empty_subtitle)
        )
    }

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

    if (showClearDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.dialog_clear_all_title),
            message = stringResource(R.string.dialog_clear_all_message),
            onConfirm = {
                when (contentType) {
                    ContentType.TRANSLATION -> translationViewModel.clearAllItems()
                    ContentType.OCR -> ocrViewModel.clearAllItems()
                    else -> {}
                }
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TopBar(
                title = viewResources.title,
                onLeadingIconClick = onBackClick,
                actions = {
                    CustomFilledIconButton(
                        icon = R.drawable.icon_delete,
                        contentDescription = stringResource(R.string.action_clear_history),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp),
                        onClick = { showClearDialog = true }
                    )
                }
            )

            CustomTabs(
                tabs = listOf(
                    TabItem(R.string.history_title, R.drawable.icon_history),
                    TabItem(R.string.bookmarks_title, R.drawable.icon_bookmark_outline),
                ),
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            CustomSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it }
            )
        }

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

enum class ContentType {
    TRANSLATION, OCR, PHRASEBOOK, DIALOG, STACKCARD
}

enum class ViewType {
    BOOKMARKS, HISTORY
}