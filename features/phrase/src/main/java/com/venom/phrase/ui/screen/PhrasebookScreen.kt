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
) {
    val state by viewModel.state.collectAsState()
    val langState by langSelectorViewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }


    LaunchedEffect(langState.sourceLang, langState.targetLang) {
        viewModel.updateLanguages(langState.sourceLang, langState.targetLang)
        viewModel.loadCategories()
    }

    PhrasebookContent(
        state = state,
        langSelectorViewModel = langSelectorViewModel,
        onNavigateToCategory = { categoryId ->
            selectedCategoryId = categoryId
        },
        scrollBehavior = scrollBehavior
    )

    selectedCategoryId?.let { categoryId ->
        PhrasesDialog(
            categoryId = categoryId,
            onDismiss = { selectedCategoryId = null }
        )
    }
}
