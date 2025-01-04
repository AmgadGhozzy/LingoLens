package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.ui.components.inputs.CustomSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseScreenContent(
    state: PhraseUiState,
    onSearchQueryChanged: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(title = {
            Text(
                state.selectedCategory?.englishEn ?: "", fontWeight = FontWeight.Bold
            )
        }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ), actions = {
            CustomSearchBar(
                modifier = Modifier.width(224.dp),
                searchQuery = state.searchQuery,
                onSearchQueryChanged = onSearchQueryChanged
            )
        })
    }) { padding ->
        SectionWithPhrasesList(
            sections = state.filteredSections,
            sourceLang = state.sourceLang.code,
            targetLang = state.targetLang.code,
            contentPadding = padding
        )
    }
}

