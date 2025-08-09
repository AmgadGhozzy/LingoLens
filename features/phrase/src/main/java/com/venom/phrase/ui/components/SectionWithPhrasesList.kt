package com.venom.phrase.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.Phrase
import com.venom.phrase.data.model.SectionWithPhrases
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.other.GlassCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SectionWithPhrasesList(
    sections: List<SectionWithPhrases>,
    sourceLang: String,
    targetLang: String,
    onBookmarkClick: (Phrase) -> Unit,
    isSpeaking: Boolean,
    onSpeakClick: (String, String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {

    var showCardDialog by remember { mutableStateOf(false) }

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
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        solidBackgroundAlpha = 0.9f,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DynamicStyledText(
                                text = section.section.getTranslation(sourceLang),
                                maxFontSize = 28,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .semantics { heading() })

                            CustomButton(
                                icon = R.drawable.icon_cards1,
                                contentDescription = stringResource(R.string.card_sentence_practice),
                                onClick = {
                                    showCardDialog = !showCardDialog
                                },
                                showBorder = false,
                            )
                        }
                    }
                }

                items(
                    items = section.phrases,
                    key = { it.phraseId }
                ) { phrase ->
                    AnimatedVisibility(visible = true, enter = fadeIn()) {
                        PhraseExpandCard(
                            phrase = phrase,
                            sourceLang = sourceLang,
                            targetLang = targetLang,
                            onBookmarkClick = { onBookmarkClick(phrase) },
                            isSpeaking = isSpeaking,
                            onSpeakClick = {
                                onSpeakClick(
                                    phrase.getTranslation(sourceLang), sourceLang
                                )
                            },
                            onCopy = { onCopy(phrase.getTranslation(sourceLang)) },
                            onShare = { onShare(phrase.getTranslation(sourceLang)) })
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showCardDialog) Dialog(
        onDismissRequest = { showCardDialog = false }, properties = DialogProperties(
            usePlatformDefaultWidth = false, decorFitsSystemWindows = false
        )
    ) {

    }
}
