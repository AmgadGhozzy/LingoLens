package com.venom.phrase.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.venom.ui.components.buttons.ExpandIndicator
import com.venom.ui.components.other.GlassCard

@Composable
fun PhraseExpandCard(
    phrase: Phrase,
    sourceLang: String,
    targetLang: String,
    onBookmarkClick: () -> Unit,
    isSpeaking: Boolean,
    onSpeakClick: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(
        onClick = { expanded = !expanded },
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Phrase card for ${phrase.englishEn}" }
            .animateContentSize(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            PhraseCardHeader(
                phrase = phrase,
                sourceLang = sourceLang,
                isSpeaking = isSpeaking,
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
