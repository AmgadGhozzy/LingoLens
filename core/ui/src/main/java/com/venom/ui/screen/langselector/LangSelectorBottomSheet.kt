package com.venom.ui.screen.langselector

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.data.model.LanguageItem
import com.venom.resources.R
import com.venom.ui.components.common.CustomBottomSheet
import com.venom.ui.components.common.EmptyState
import com.venom.ui.components.common.adp
import com.venom.ui.components.inputs.CustomSearchBar
import com.venom.ui.components.other.FadeOverlay
import com.venom.ui.components.other.GlassCard
import com.venom.ui.components.other.GlassThickness

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LangSelectorBottomSheet(
    viewModel: LangSelectorViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    CustomBottomSheet(onDismiss) {
        LangSelectorContent(
            viewModel = viewModel,
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
    viewModel: LangSelectorViewModel,
    state: LanguageSelectorState,
    onSearchQueryChange: (String) -> Unit,
    onLanguageSelected: (LanguageItem) -> Unit,
    onDownloadLanguage: (LanguageItem) -> Unit,
    onDeleteLanguage: (LanguageItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.adp)
    ) {
        // Currently Selecting Indicator
        Text(
            text = stringResource(
                id = if (state.isSelectingSourceLanguage)
                    R.string.select_source_language
                else
                    R.string.select_target_language
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.adp)
        )

        // Language Bar
        GlassCard(

            modifier = Modifier.fillMaxWidth(),
            thickness = GlassThickness.Regular,
            shape = RoundedCornerShape(20.adp),
            contentPadding = 12.adp
        ) {
            LanguageBar(
                langSelectorViewModel = viewModel,
                isFromBottomSheet = true,
                showNativeNameHint = false,
                showFlag = true
            )
        }

        Spacer(modifier = Modifier.height(16.adp))

        // Search Bar
        CustomSearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChanged = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.adp))

        // Language List
        AnimatedContent(
            targetState = state.filteredLanguages.isNotEmpty(),
            label = "language_list"
        ) { hasLanguages ->
            if (hasLanguages) {
                val listState = rememberLazyListState()

                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.adp),
                        verticalArrangement = Arrangement.spacedBy(10.adp)
                    ) {
                        items(
                            items = state.filteredLanguages,
                            key = { it.code }
                        ) { language ->
                            LanguageListItem(
                                language = language,
                                isSelected = language == (
                                        if (state.isSelectingSourceLanguage)
                                            state.sourceLang
                                        else
                                            state.targetLang
                                        ),
                                onClick = { onLanguageSelected(language) },
                                onDownloadClick = { onDownloadLanguage(language) },
                                onDeleteClick = { onDeleteLanguage(language) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(40.adp))
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