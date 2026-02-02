package com.venom.phrase.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.venom.phrase.data.model.PhraseEntity
import com.venom.ui.components.buttons.ExpandIndicator
import com.venom.ui.components.common.adp
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.components.other.GradientGlassCard

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
    GradientGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Phrase card for ${phrase.englishEn}" }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        onClick = { onExpandChange(!isExpanded) },
        thickness = if (isExpanded) GlassThickness.Thick else GlassThickness.Regular,
        gradientAlpha = if (isExpanded) 0.15f else 0.08f,
        shape = RoundedCornerShape(20.adp),
        showBorder = isExpanded,
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(16.adp),
            verticalArrangement = Arrangement.spacedBy(0.adp)
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
                onClick = { onExpandChange(!isExpanded) },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f),
                containerColor = Color.Transparent
            )
        }
    }
}