package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.model.Phrase
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseExpandCard(phrase: Phrase, sourceLang: String, targetLang: String) {
    var expanded by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Phrase card for ${phrase.englishEn}" },
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
            sourceLang = sourceLang,
            targetLang = targetLang,
            onFavoriteClick = { isFavorite = !isFavorite })

        CustomButton(
            icon = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
            onClick = { expanded = !expanded },
            contentDescription = stringResource(
                if (expanded) R.string.action_collapse
                else R.string.action_expand
            ),
            modifier = Modifier
                .align(Alignment.End)
                .fillMaxWidth()
        )
    }
}
