package com.venom.wordcard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.ui.viewmodel.TranslateViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.wordcard.data.model.WordEntity
import com.venom.wordcard.ui.viewmodel.WordSwiperEvent
import com.venom.wordcard.ui.viewmodel.WordSwiperViewModel

@Composable
fun WordCardScreen(
    viewModel: WordSwiperViewModel = hiltViewModel(),
    translateViewModel: TranslateViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsState()
    val translateState by translateViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val copyAction: (WordEntity) -> Unit = { text -> context.copyToClipboard(text.englishWord) }
    val speakAction: (WordEntity) -> Unit = { text -> ttsViewModel.speak(text.englishWord) }

    val onBookmarkWord: (WordEntity) -> Unit = { viewModel.onEvent(WordSwiperEvent.Bookmark(it)) }
    val onRememberWord: (WordEntity) -> Unit = { viewModel.onEvent(WordSwiperEvent.Remember(it)) }
    val onForgotWord: (WordEntity) -> Unit = { viewModel.onEvent(WordSwiperEvent.Forgot(it)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 124.dp, start = 42.dp, end = 42.dp),
        contentAlignment = Alignment.Center
    ) {
        WordSwiperStack(
            viewModel = viewModel,
            onRememberWord = onRememberWord,
            onForgotWord = onForgotWord,
            onBookmarkWord = onBookmarkWord,
            onSpeakWord = speakAction,
            onCopyWord = copyAction,
            onUndoLastAction = { },
        )
    }
}
