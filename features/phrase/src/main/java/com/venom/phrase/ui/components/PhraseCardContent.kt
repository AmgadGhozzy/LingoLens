package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.model.Phrase

@Composable
fun PhraseCardContent(
    phrase: Phrase,
    expanded: Boolean,
    sourceLang: String,
    targetLang: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PhraseCardHeader(
            phrase = phrase,
            sourceLang = sourceLang,
            isFavorite = isFavorite,
            onFavoriteClick = onFavoriteClick
        )

        ExpandedContent(
            visible = expanded, phrase = phrase,
            targetLang = targetLang,
        )
    }
}
