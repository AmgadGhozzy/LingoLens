package com.venom.lingopro.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.lingopro.R
import com.venom.lingopro.domain.model.LanguageItem
import com.venom.lingopro.ui.components.common.EmptyState
import com.venom.lingopro.ui.components.bars.LanguageBar
import com.venom.lingopro.ui.components.items.LanguageListItem
import com.venom.lingopro.ui.components.inputs.SearchBar
import com.venom.lingopro.utils.Constants.LANGUAGES_LIST
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectorBottomSheet(
    languages: List<LanguageItem> = LANGUAGES_LIST,
    initialSourceLang: LanguageItem,
    initialTargetLang: LanguageItem,
    isSelectingSourceLanguage: Boolean, // Add this parameter
    onLanguageSelected: (LanguageItem) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedLanguage by remember {
        mutableStateOf(
            if (isSelectingSourceLanguage) initialSourceLang
            else initialTargetLang
        )
    }
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val maxHeight = screenHeight * 0.9f
    val offsetY = screenHeight * 0.1f

    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()


    var sourceLang by remember { mutableStateOf(initialSourceLang) }
    var targetLang by remember { mutableStateOf(initialTargetLang) }

    // Filter languages based on search query
    val filteredLanguages = remember(searchQuery, languages) {
        languages.filter {
            searchQuery.isEmpty() || listOf(it.name, it.nativeName).any { name ->
                name.contains(
                    searchQuery, ignoreCase = true
                )
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch { sheetState.hide() }
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier
            .fillMaxHeight(maxHeight)
            .navigationBarsPadding()
            .offset(y = offsetY.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            LanguageBar(
                sourceLang = sourceLang,
                targetLang = targetLang,
                isFromBottomSheet = true,
                onSwapLanguages = {
                    // Swap languages when swap button is pressed
                    val temp = sourceLang
                    sourceLang = targetLang
                    targetLang = temp
                },
                onLanguageSelect = { isSource, _ ->
                },
                showNativeNameHint = false,
                showFlag = true
            )

            // Search Bar
            SearchBar(searchQuery = searchQuery, onSearchQueryChanged = { searchQuery = it })

            // Language List Section
            LanguageListSection(filteredLanguages = filteredLanguages,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { language ->
                    sourceLang = language
                    targetLang = language
                    selectedLanguage = language
                    onLanguageSelected(language)
                    // Close the bottom sheet
                    coroutineScope.launch {
                        sheetState.hide()
                        onDismiss()
                    }

                })
        }
    }
}

@Composable
private fun LanguageListSection(
    filteredLanguages: List<LanguageItem>,
    selectedLanguage: LanguageItem?,
    onLanguageSelected: (LanguageItem) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        if (filteredLanguages.isNotEmpty()) {
            // Language list items
            items(filteredLanguages, key = { it.code }) { language ->
                LanguageListItem(language = language,
                    isSelected = language == selectedLanguage,
                    onClick = { onLanguageSelected(language) })
            }
        } else {
            // Empty state item
            item {
                EmptyState(
                    icon = R.drawable.icon_search,
                    title = stringResource(R.string.search_no_results_title),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun LanguageSelectorBottomSheetPreview() {
    MaterialTheme {
        LanguageSelectorBottomSheet(onDismiss = {},
            onLanguageSelected = { _ -> },
            initialSourceLang = LANGUAGES_LIST[0],
            initialTargetLang = LANGUAGES_LIST[1],
            isSelectingSourceLanguage = true
        )
    }
}