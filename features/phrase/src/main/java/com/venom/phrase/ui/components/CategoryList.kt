package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.model.Category
import com.venom.phrase.ui.viewmodel.PhraseUiState

@Composable
fun CategoryList(
    state: PhraseUiState, onCategoryClick: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.categories) { category ->
            CategoryItemCard(state = state,
                category = category,
                onClick = { category.categoryId?.let(onCategoryClick) })
        }
    }
}