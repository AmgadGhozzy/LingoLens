package com.venom.phrase.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.model.PhraseEntity
import com.venom.ui.components.buttons.ExpandIndicator
import com.venom.ui.components.other.GlassCard

@Composable
fun PhraseExpandCard(
    phrase: PhraseEntity,
    sourceLang: String,
    targetLang: String,
    onBookmarkClick: () -> Unit,
    isSpeaking: Boolean,
    onSpeakClick: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        onClick = {
            onExpandChange(!isExpanded)
        },
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
                visible = isExpanded,
                phrase = phrase,
                targetLang = targetLang,
                onCopyClick = onCopy,
                onShareClick = onShare
            )

            ExpandIndicator(
                expanded = isExpanded,
                onClick = {
                    onExpandChange(!isExpanded)
                },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}