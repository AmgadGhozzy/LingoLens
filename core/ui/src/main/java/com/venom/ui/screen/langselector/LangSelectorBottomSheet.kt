package com.venom.ui.screen.langselector

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.data.model.LanguageItem
import com.venom.resources.R
import com.venom.ui.components.common.EmptyState
import com.venom.ui.components.inputs.CustomSearchBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LangSelectorBottomSheet(
    viewModel: LangSelectorViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val configuration = LocalConfiguration.current

    val bottomSheetHeight = remember(configuration) {
        configuration.screenHeightDp * 0.8f
    }


    LaunchedEffect(Unit) {
        sheetState.show()
    }

    ModalBottomSheet(
        onDismissRequest = { scope.launch { sheetState.hide(); onDismiss() } },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier
            .fillMaxHeight(bottomSheetHeight)
            .navigationBarsPadding()
    ) {
        LangSelectorContent(
            state = state,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onLanguageSelected = { language ->
                viewModel.onLanguageSelected(language)
                scope.launch { sheetState.hide(); onDismiss() } 
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
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
            .padding(horizontal = 16.dp)
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
            targetState = state.filteredLanguages, label = "Language List Animation"
        ) { languages ->
            if (languages.isEmpty()) {
                EmptyState(
                    icon = R.drawable.icon_search,
                    title = stringResource(R.string.search_no_results_title),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(languages) { language ->
                        LanguageListItem(
                            language = language,
                            isSelected = language == (if (state.isSelectingSourceLanguage) state.sourceLang else state.targetLang),
                            onClick = { onLanguageSelected(language) },
                            onDownloadClick = { onDownloadLanguage(language) },
                            onDeleteClick = { onDeleteLanguage(language) })
                    }
                }
            }
        }
    }
}

