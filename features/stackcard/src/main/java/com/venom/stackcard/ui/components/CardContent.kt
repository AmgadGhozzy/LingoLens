package com.venom.stackcard.ui.components

import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.stackcard.ui.viewmodel.CardItem
import com.venom.ui.viewmodel.SettingsViewModel
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.ui.viewmodel.TranslateViewModel

@Composable
fun CardContent(
    card: CardItem,
    isFlipped: Boolean,
    modifier: Modifier = Modifier,
    translateViewModel: TranslateViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    settingsViewModel: SettingsViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    ttsViewModel: TTSViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    val state by translateViewModel.uiState.collectAsStateWithLifecycle()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val rememberedCard = remember(card.englishEn) { card }

    // Clear translation terms when card changes or when loading starts
    val wordTerms by remember(state.translationResult, state.isLoading, rememberedCard.englishEn) {
        derivedStateOf {
            if (state.isLoading || state.translationResult.dict.isEmpty()) {
                ""
            } else {
                state.translationResult.dict.flatMap { it.terms }.take(5).joinToString(", ")
            }
        }
    }

    LaunchedEffect(isFlipped, rememberedCard.englishEn) {
        if (isFlipped) {
            translateViewModel.clearAll()
            translateViewModel.onSourceTextChanged(rememberedCard.englishEn)
        }
    }

    LaunchedEffect(rememberedCard.englishEn) {
        translateViewModel.clearAll()
    }

    // Auto-speak when card changes based on setting
    LaunchedEffect(rememberedCard.englishEn) {
        if (settingsState.autoPronunciation) {
            ttsViewModel.speak(rememberedCard.englishEn)
        }
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