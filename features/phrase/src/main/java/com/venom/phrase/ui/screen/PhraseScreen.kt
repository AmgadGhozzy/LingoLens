package com.venom.phrase.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.phrase.ui.components.PhraseScreenContent
import com.venom.phrase.ui.viewmodel.PhraseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasesScreen(
    viewModel: PhraseViewModel = hiltViewModel(), categoryId: Int
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(categoryId) {
        viewModel.loadeSectionsWithPhrases(categoryId)
    }

    PhraseScreenContent(state = state, scrollBehavior = scrollBehavior)
}