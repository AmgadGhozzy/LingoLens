package com.venom.phrase.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.phrase.ui.components.PhraseScreenContent
import com.venom.phrase.ui.viewmodel.PhraseViewModel
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasesScreen(
    viewModel: PhraseViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    categoryId: Int,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val ttsState by ttsViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(categoryId) {
        if (categoryId == -1) {
            viewModel.loadBookmarkedPhrases()
        } else {
            viewModel.loadSectionsWithPhrases(categoryId)
        }
    }

    val copyAction: (String) -> Unit = { text -> context.copyToClipboard(text) }
    val shareAction: (String) -> Unit = { text -> context.shareText(text) }

    PhraseScreenContent(
        state = state,
        scrollBehavior = scrollBehavior,
        onBookmarkClick = viewModel::toggleBookmark,
        isSpeakingText = ttsState::isSpeakingText,
        onSpeakClick = ttsViewModel::speak,
        onSearchQueryChanged = viewModel::onSearchQueryChange,
        onCopy = copyAction,
        onShare = shareAction,
        onDismiss = onDismiss
    )
}
