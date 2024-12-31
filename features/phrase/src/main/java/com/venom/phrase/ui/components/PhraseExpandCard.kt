package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.model.Phrase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseExpandCard(phrase: Phrase) {
    var expanded by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Phrase card for ${phrase.enUS}" },
        shape = RoundedCornerShape(16.dp),
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp, pressedElevation = 4.dp
        )
    ) {
        PhraseCardContent(phrase = phrase,
            expanded = expanded,
            isFavorite = isFavorite,
            onFavoriteClick = { isFavorite = !isFavorite })
    }
}
