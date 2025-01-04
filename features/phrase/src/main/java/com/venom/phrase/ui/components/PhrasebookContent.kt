package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.ui.viewmodel.LangSelectorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasebookContent(
    state: PhraseUiState,
    langSelectorViewModel: LangSelectorViewModel,
    onNavigateToCategory: (Int) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        PhrasebookTopBar(
            viewModel = langSelectorViewModel,
            totalPhrases = state.categories.sumOf { it.phraseCount },
            scrollBehavior = scrollBehavior,
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryList(state = state, onCategoryClick = onNavigateToCategory)
        }
    }
}
