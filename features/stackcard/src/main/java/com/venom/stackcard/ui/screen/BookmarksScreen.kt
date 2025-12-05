package com.venom.stackcard.ui.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.resources.R
import com.venom.stackcard.ui.components.BookmarkWordItem
import com.venom.stackcard.ui.viewmodel.BookmarksViewModel
import com.venom.ui.components.common.EmptyState
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val ttsViewModel: TTSViewModel = hiltViewModel()
    val bookmarkedWords by viewModel.bookmarkedWords.collectAsStateWithLifecycle()

    // Single state to track which item is expanded (by word ID)
    var expandedWordId by remember { mutableStateOf<Int?>(null) }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                stringResource(R.string.words_bookmarks),
                style = MaterialTheme.typography.headlineSmall
            )
        }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        })
    }) { padding ->
        AnimatedContent(
            targetState = bookmarkedWords.isEmpty()
        ) { isEmpty ->
            if (isEmpty) {
                EmptyState(
                    icon = R.drawable.icon_cards2,
                    title = stringResource(id = R.string.words_title),
                    subtitle = stringResource(id = R.string.words_bookmarks_empty_state_subtitle),
                )
            } else {
                LazyColumn(
                    contentPadding = padding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(
                        count = bookmarkedWords.size,
                        key = { index -> bookmarkedWords[index].id }
                    ) { index ->
                        val word = bookmarkedWords[index]
                        BookmarkWordItem(
                            word = word,
                            isExpanded = expandedWordId == word.id,
                            onExpandChange = { isExpanding ->
                                expandedWordId = if (isExpanding) word.id else null
                            },
                            onSpeak = { ttsViewModel.speak(word.englishEn) },
                            onCopy = { context.copyToClipboard(word.englishEn) },
                            onBookmark = { viewModel.removeBookmark(word) },
                            showAll = false,
                        )
                    }
                }
            }
        }
    }
}