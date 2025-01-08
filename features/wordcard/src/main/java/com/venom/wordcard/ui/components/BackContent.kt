package com.venom.wordcard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.ui.components.common.PulseAnimation
import com.venom.ui.viewmodel.TranslateViewModel
import com.venom.wordcard.data.model.WordEntity

@Composable
fun BackContent(
    translateViewModel: TranslateViewModel = hiltViewModel(), card: WordEntity
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = card.arabicWord,
            fontSize = 42.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        card.synonyms.take(5).forEach { synonyms ->
            translateViewModel.onUserInputChanged(card.englishWord)
            if (!translateViewModel.uiState.value.isLoading) {
                Text(
                    text = translateViewModel.uiState.value.synonyms.toString(),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            } else PulseAnimation()
        }
    }
}