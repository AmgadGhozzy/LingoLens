package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.stackcard.ui.viewmodel.CardItem
import com.venom.ui.viewmodel.TranslateViewModel

@Composable
fun CardContent(
    card: CardItem,
    isFlipped: Boolean,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
    translateViewModel: TranslateViewModel = hiltViewModel()
) {
    val state by translateViewModel.uiState.collectAsStateWithLifecycle()
    val rememberedCard = remember(card.englishEn) { card }

    // Clear translation terms when card changes or when loading starts
    val wordTerms by remember(state.translationResult, state.isLoading, rememberedCard.englishEn) {
        derivedStateOf {
            // Only show terms if not loading and we have translation results for the current card
            if (state.isLoading || state.translationResult.dict.isEmpty()) {
                ""
            } else {
                state.translationResult.dict.flatMap { it.terms }.take(5).joinToString(", ")
            }
        }
    }

    LaunchedEffect(isFlipped, rememberedCard.englishEn) {
        if (isFlipped) {
            // Clear old translation when starting new translation
            translateViewModel.clearAll() // You'll need to add this method to your ViewModel
            translateViewModel.onSourceTextChanged(rememberedCard.englishEn)
        }
    }

    // Clear translation when card changes (can be triggered by onDrag)
    LaunchedEffect(rememberedCard.englishEn) {
        translateViewModel.clearAll()
    }

    Column(
        modifier = modifier.padding(top = 42.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CardText(
            text = if (isFlipped) rememberedCard.arabicAr else rememberedCard.englishEn,
            wordTerms = wordTerms,
            isLoading = state.isLoading,
            isFlipped = isFlipped
        )
    }
}