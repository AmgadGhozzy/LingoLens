package com.venom.ui.screen.langselector

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.data.model.LanguageItem
import com.venom.resources.R
import com.venom.ui.components.common.CustomBottomSheet
import com.venom.ui.components.common.EmptyState
import com.venom.ui.components.inputs.CustomSearchBar
import com.venom.ui.components.other.FadeOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LangSelectorBottomSheet(
    viewModel: LangSelectorViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    CustomBottomSheet(onDismiss) {
        LangSelectorContent(
            state = state,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onLanguageSelected = { language ->
                viewModel.onLanguageSelected(language)
                onDismiss()
            },
            onDownloadLanguage = viewModel::downloadLanguage,
            onDeleteLanguage = viewModel::deleteLanguage
        )
    }
}

@Composable
fun LangSelectorContent(
    state: LanguageSelectorState,
    onSearchQueryChange: (String) -> Unit,
    onLanguageSelected: (LanguageItem) -> Unit,
    onDownloadLanguage: (LanguageItem) -> Unit,
    onDeleteLanguage: (LanguageItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp).background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        LanguageBar(
            viewModel = hiltViewModel(),
            isFromBottomSheet = true,
            showNativeNameHint = false,
            showFlag = true
        )

        CustomSearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChanged = onSearchQueryChange,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        AnimatedContent(
            targetState = state.filteredLanguages,
        ) { languages ->
            if (languages.isNotEmpty()) {
                val listState = rememberLazyListState()

                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(languages) { language ->
                            LanguageListItem(
                                language = language,
                                isSelected = language == (
                                        if (state.isSelectingSourceLanguage) state.sourceLang else state.targetLang),
                                onClick = { onLanguageSelected(language) },
                                onDownloadClick = { onDownloadLanguage(language) },
                                onDeleteClick = { onDeleteLanguage(language) }
                            )
                        }
                    }
                    FadeOverlay(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        top = false
                    )
                }
            } else {
                EmptyState(
                    icon = R.drawable.icon_search,
                    title = stringResource(R.string.search_no_results_title),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

