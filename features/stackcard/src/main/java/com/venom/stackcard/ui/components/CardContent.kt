package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
    card: CardItem, translateViewModel: TranslateViewModel = hiltViewModel(), isFlipped: Boolean
) {
    val state by translateViewModel.uiState.collectAsStateWithLifecycle()
    val rememberedCard = remember(card.englishEn) { card }

    val translationText by remember(state.translationResult) {
        derivedStateOf {
            state.translationResult.dict?.flatMap { it.terms }?.joinToString(", ")
        }
    }

    LaunchedEffect(isFlipped, rememberedCard.englishEn) {
        if (isFlipped) {
            translateViewModel.onSourceTextChanged(rememberedCard.englishEn)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = if (isFlipped) 24.dp else 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CardText(
            text = if (isFlipped) rememberedCard.arabicAr else rememberedCard.englishEn,
            translationText = translationText,
            isLoading = state.isLoading,
            isFlipped = isFlipped
        )
    }
}