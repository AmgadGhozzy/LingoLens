package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.Phrase
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.resources.R
import com.venom.ui.components.common.EmptyState
import com.venom.ui.components.inputs.CustomSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseScreenContent(
    state: PhraseUiState,
    onSearchQueryChanged: (String) -> Unit,
    onBookmarkClick: (Phrase) -> Unit,
    onSpeakClick: (String, String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.selectedCategory?.getTranslation(state.sourceLang.code).orEmpty(),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    CustomSearchBar(
                        modifier = Modifier.width(224.dp),
                        searchQuery = state.searchQuery,
                        onSearchQueryChanged = onSearchQueryChanged
                    )
                }
            )
        }
    ) { padding ->
        if (state.filteredSections.isNotEmpty()) {
            SectionWithPhrasesList(
                sections = state.filteredSections,
                sourceLang = state.sourceLang.code,
                targetLang = state.targetLang.code,
                onBookmarkClick = onBookmarkClick,
                onSpeakClick = onSpeakClick,
                onCopy = onCopy,
                onShare = onShare,
                contentPadding = padding
            )
        } else if (state.searchQuery.isNotBlank()) {
            EmptyState(
                modifier = Modifier.fillMaxSize(),
                icon = R.drawable.icon_dialog,
                title = stringResource(R.string.phrase_empty_state_title),
                subtitle = stringResource(R.string.phrase_empty_state_subtitle)
            )
        }
    }
}
