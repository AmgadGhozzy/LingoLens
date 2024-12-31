package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.model.Phrase

@Composable
fun PhrasesList(
    phrases: List<Phrase>, contentPadding: PaddingValues
) {
    LazyColumn(
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 16.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = phrases, key = { it.id }) { phrase ->
            PhraseExpandCard(phrase = phrase)
        }
    }
}