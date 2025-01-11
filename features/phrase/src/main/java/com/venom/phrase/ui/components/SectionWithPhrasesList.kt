package com.venom.phrase.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.Phrase
import com.venom.phrase.data.model.SectionWithPhrases
import com.venom.ui.components.common.DynamicStyledText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SectionWithPhrasesList(
    sections: List<SectionWithPhrases>,
    sourceLang: String,
    targetLang: String,
    onBookmarkClick: (Phrase) -> Unit,
    onSpeakClick: (String, String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            sections.filter { it.phrases.isNotEmpty() }.forEach { section ->
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        DynamicStyledText(text = section.section.getTranslation(sourceLang),
                            maxFontSize = 28,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { heading() })
                    }
                }

                items(section.phrases, key = { it.phraseId }) { phrase ->
                    PhraseExpandCard(phrase = phrase,
                        sourceLang = sourceLang,
                        targetLang = targetLang,
                        onBookmarkClick = { onBookmarkClick(phrase) },
                        onSpeakClick = {
                            onSpeakClick(
                                phrase.getTranslation(sourceLang), sourceLang
                            )
                        },
                        onCopy = { onCopy(phrase.getTranslation(sourceLang)) },
                        onShare = { onShare(phrase.getTranslation(sourceLang)) })
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
