package com.venom.phrase.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.model.Phrase

@Composable
fun PhraseExpandCard(
    phrase: Phrase,
    sourceLang: String,
    targetLang: String,
    onBookmarkClick: () -> Unit,
    onSpeakClick: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Phrase card for ${phrase.englishEn}" }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(16.dp),
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
        ) {
            PhraseCardHeader(
                phrase = phrase,
                sourceLang = sourceLang,
                isBookmarked = phrase.isBookmarked,
                onBookmarkClick = onBookmarkClick,
                onSpeakClick = onSpeakClick
            )

            ExpandedContent(
                visible = expanded,
                phrase = phrase,
                targetLang = targetLang,
                onCopyClick = onCopy,
                onShareClick = onShare
            )

            ExpandIndicator(
                expanded = expanded,
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
