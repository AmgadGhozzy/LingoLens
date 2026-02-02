package com.venom.phrase.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.ui.components.common.adp

@Composable
fun CategoryList(
    state: PhraseUiState,
    onCategoryClick: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.adp,
            end = 16.adp,
            top = 16.adp,
            bottom = 100.adp
        ),
        verticalArrangement = Arrangement.spacedBy(12.adp)
    ) {
        itemsIndexed(
            items = state.categories,
            key = { _, category -> category.categoryId }
        ) { index, category ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                CategoryItemCard(
                    state = state,
                    category = category,
                    onClick = { category.categoryId.let(onCategoryClick) }
                )
            }
        }
    }
}