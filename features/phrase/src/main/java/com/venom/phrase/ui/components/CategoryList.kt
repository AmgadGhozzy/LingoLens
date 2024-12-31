package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.model.Category

@Composable
fun CategoryList(
    categories: List<Category>, onCategoryClick: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryItemCard(category = category,
                onClick = { category.categoryId?.let(onCategoryClick) })
        }
    }
}