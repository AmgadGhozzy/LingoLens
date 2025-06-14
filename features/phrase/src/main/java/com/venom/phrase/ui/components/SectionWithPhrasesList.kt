package com.venom.phrase.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        DynamicStyledText(text = section.section.getTranslation(sourceLang),
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
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }

                items(
                    items = section.phrases,
                    key = { it.phraseId }
                ) { phrase ->
                    AnimatedVisibility(visible = true, enter = fadeIn()) {
                        PhraseExpandCard(phrase = phrase,
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
