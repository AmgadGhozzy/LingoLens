package com.venom.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.venom.data.local.entity.OcrEntity
import com.venom.domain.model.TranslationResult
import com.venom.domain.model.ViewResources
import com.venom.resources.R
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.dialogs.ConfirmationDialog
import com.venom.ui.components.inputs.CustomSearchBar
import com.venom.ui.components.sections.CustomTabs
import com.venom.ui.components.sections.TabItem
import com.venom.ui.navigation.Screen
import com.venom.ui.screen.history.OcrBookmarkList
import com.venom.ui.screen.history.TransBookmarkList
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
    navController: NavController,
    onBackClick: () -> Unit,
    onTranslationItemClick: (TranslationResult) -> Unit = { result ->
        navController.navigate(Screen.Translation.createRoute(result.sourceText))
    },
    onOcrItemClick: (OcrEntity) -> Unit = { ocrEntry ->
        try {
            navController.navigate("ocr") {
                popUpTo("ocr") {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        } catch (e: Exception) {
            println("OCR navigation failed: ${e.message}")
        }
    }
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

    Column(
        modifier = Modifier.fillMaxSize()
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
                            containerColor = MaterialTheme.colorScheme.error.copy(0.1f),
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
                    onItemClick = onTranslationItemClick,
                    onOpenInNew = onTranslationItemClick
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
                    onItemClick = onOcrItemClick,
                    onOpenInNew = onOcrItemClick
                )
            }

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
}

enum class ContentType {
    TRANSLATION, OCR, PHRASEBOOK, DIALOG, STACKCARD
}

enum class ViewType {
    BOOKMARKS, HISTORY
}