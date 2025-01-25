package com.venom.stackcard.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.resources.R
import com.venom.stackcard.data.model.WordEntity
import com.venom.stackcard.ui.components.BookmarkWordItem
import com.venom.stackcard.ui.viewmodel.BookmarksViewModel
import com.venom.ui.components.common.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel = hiltViewModel(),
    onCopy: (WordEntity) -> Unit,
    onSpeak: (WordEntity) -> Unit,
    onBackClick: () -> Unit
) {
    val bookmarkedWords by viewModel.bookmarkedWords.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                "Bookmarked Words", style = MaterialTheme.typography.headlineSmall
            )
        }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        })
    }) { padding ->
        AnimatedContent(
            targetState = bookmarkedWords.isEmpty(), label = "Bookmarks Content"
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
                        key = { index -> bookmarkedWords[index].id }) { index ->
                        val word = bookmarkedWords[index]
                        BookmarkWordItem(
                            word = word,
                            onSpeak = { onSpeak(word) },
                            onCopy = { onCopy(word) },
                            showAll = false,
                        )
                    }
                }
            }
        }
    }
}
