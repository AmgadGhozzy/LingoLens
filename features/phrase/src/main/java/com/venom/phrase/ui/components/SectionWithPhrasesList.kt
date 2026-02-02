package com.venom.phrase.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.PhraseEntity
import com.venom.phrase.data.model.SectionWithPhrases
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.common.adp
import com.venom.ui.components.other.GlassCard
import com.venom.ui.components.other.GlassThickness

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SectionWithPhrasesList(
    sections: List<SectionWithPhrases>,
    sourceLang: String,
    targetLang: String,
    onBookmarkClick: (PhraseEntity) -> Unit,
    isSpeakingText: (String) -> Boolean,
    onSpeakClick: (String, String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    // Track which phrase is expanded (using phrase ID)
    var expandedPhraseId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.adp,
                end = 16.adp,
                top = 8.adp,
                bottom = 24.adp
            ),
            verticalArrangement = Arrangement.spacedBy(12.adp)
        ) {
            sections.filter { it.phrases.isNotEmpty() }.forEach { section ->
                stickyHeader(key = "section_${section.section.sectionId}") {
                    SectionHeader(
                        title = section.section.getTranslation(sourceLang),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                itemsIndexed(
                    items = section.phrases,
                    key = { _, phrase -> phrase.phraseId }
                ) { index, phrase ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    ) {
                        PhraseExpandCard(
                            phrase = phrase,
                            sourceLang = sourceLang,
                            targetLang = targetLang,
                            onBookmarkClick = { onBookmarkClick(phrase) },
                            isSpeaking = isSpeakingText(phrase.getTranslation(sourceLang)),
                            onSpeakClick = {
                                onSpeakClick(
                                    phrase.getTranslation(sourceLang), sourceLang
                                )
                            },
                            onCopy = { onCopy(phrase.getTranslation(sourceLang)) },
                            onShare = { onShare(phrase.getTranslation(sourceLang)) },
                            isExpanded = expandedPhraseId == phrase.phraseId.toString(),
                            onExpandChange = { isExpanding ->
                                expandedPhraseId =
                                    if (isExpanding) phrase.phraseId.toString() else null
                            }
                        )
                    }
                }

                // Section spacing
                item(key = "spacer_${section.section.sectionId}") {
                    Spacer(modifier = Modifier.height(8.adp))
                }
            }
        }
    }
}


@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(vertical = 8.adp)
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            thickness = GlassThickness.UltraThick,
            shape = RoundedCornerShape(16.adp),
            contentPadding = 0.adp,
            showShadow = true,
            showBorder = false
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.adp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .padding(horizontal = 16.adp, vertical = 12.adp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    // Accent line indicator
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.adp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 3.adp, vertical = 12.adp)
                    )

                    DynamicStyledText(
                        text = title,
                        minFontSize = 20,
                        maxFontSize = 28,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.adp)
                            .semantics { heading() }
                    )
                }
            }
        }
    }
}