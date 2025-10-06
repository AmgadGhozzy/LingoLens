package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.ui.screen.langselector.LangSelectorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasebookContent(
    state: PhraseUiState,
    langSelectorViewModel: LangSelectorViewModel,
    onNavigateToCategory: (Int) -> Unit,
    onBookmarkClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        PhrasebookTopBar(
            viewModel = langSelectorViewModel,
            totalPhrases = state.categories.sumOf { it.phraseCount },
            onBookmarkClick = onBookmarkClick,
            scrollBehavior = scrollBehavior
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryList(state = state, onCategoryClick = onNavigateToCategory)
        }
    }
}