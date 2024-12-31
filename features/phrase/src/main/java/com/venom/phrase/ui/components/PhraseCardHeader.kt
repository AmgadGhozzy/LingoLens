package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.venom.phrase.data.model.Phrase

@Composable
fun PhraseCardHeader(
    phrase: Phrase, isFavorite: Boolean, onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = phrase.enUS, style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ), modifier = Modifier.weight(1f)
        )

        ExpandCardActions(
            isFavorite = isFavorite, onFavoriteClick = onFavoriteClick
        )
    }
}