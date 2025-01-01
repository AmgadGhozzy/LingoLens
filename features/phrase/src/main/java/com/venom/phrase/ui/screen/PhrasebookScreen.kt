package com.venom.phrase.ui.screen

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.phrase.ui.components.PhrasebookContent
import com.venom.phrase.ui.viewmodel.PhraseViewModel
import com.venom.ui.viewmodel.LangSelectorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasebookScreen(
    viewModel: PhraseViewModel = hiltViewModel(),
    langSelectorViewModel: LangSelectorViewModel = hiltViewModel(),
    onNavigateToCategory: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val langState by langSelectorViewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(langState.sourceLang, langState.targetLang) {
        viewModel.updateLanguages(langState.sourceLang, langState.targetLang)
    }

    PhrasebookContent(
        state = state,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        langSelectorViewModel = langSelectorViewModel,
        onNavigateToCategory = onNavigateToCategory,
        scrollBehavior = scrollBehavior
    )
}
