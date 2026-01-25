package com.venom.phrase.ui.screen

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.phrase.ui.components.PhrasebookContent
import com.venom.phrase.ui.viewmodel.PhraseViewModel
import com.venom.ui.screen.langselector.LangSelectorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasebookScreen(
    viewModel: PhraseViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    langSelectorViewModel: LangSelectorViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
) {
    val state by viewModel.state.collectAsState()
    val langState by langSelectorViewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(langState.sourceLang, langState.targetLang) {
        viewModel.updateLanguages(langState.sourceLang, langState.targetLang)
        Log.d("PhrasebookScreen", "sourceLang: ${langState.sourceLang}, targetLang: ${langState.targetLang}")
        viewModel.loadCategories()
    }

    PhrasebookContent(
        state = state,
        langSelectorViewModel = langSelectorViewModel,
        onNavigateToCategory = { categoryId ->
            selectedCategoryId = categoryId
        },
        onBookmarkClick = {
            selectedCategoryId = -1
        },
        scrollBehavior = scrollBehavior
    )

    selectedCategoryId?.let { categoryId ->
        PhrasesDialog(viewModel = viewModel, categoryId = categoryId, onDismiss = { selectedCategoryId = null })
    }
}
