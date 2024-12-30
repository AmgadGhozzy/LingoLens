package com.venom.phrase.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.phrase.ui.viewmodel.PhrasebookViewModel
import kotlinx.coroutines.launch
@Composable
fun CategoryScreen(viewModel: PhrasebookViewModel = hiltViewModel()) {
    val categories by viewModel.categories.observeAsState(emptyList())

    LazyColumn {
        items(categories) { category ->
            Text(text = category.english ?: "")
        }
    }
}

@Composable
fun PhraseScreen(sectionId: Int, viewModel: PhrasebookViewModel = hiltViewModel()) {
    val phrases by viewModel.phrases.observeAsState(emptyList())

    LazyColumn {
        items(phrases) { phrase ->
            Text(text = phrase.english ?: "")
        }
    }
}
