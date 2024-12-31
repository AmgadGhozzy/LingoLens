package com.venom.phrase.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.ui.components.bars.LanguageBar
import com.venom.ui.components.inputs.CustomSearchBar
import com.venom.ui.viewmodel.LangSelectorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasebookContent(
    state: PhraseUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    langSelectorViewModel: LangSelectorViewModel,
    onNavigateToCategory: (Int) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        PhrasebookTopBar(
            totalPhrases = state.phrases.size,
            scrollBehavior = scrollBehavior,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding()
                .navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LanguageBar(
                viewModel = langSelectorViewModel,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(28.dp)
                    )
            )

            CustomSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChange,
            )

            CategoryList(
                categories = state.categories, onCategoryClick = onNavigateToCategory
            )
        }
    }
}
