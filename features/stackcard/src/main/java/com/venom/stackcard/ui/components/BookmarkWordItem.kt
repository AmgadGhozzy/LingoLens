package com.venom.stackcard.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.resources.R
import com.venom.stackcard.data.model.WordEntity
import com.venom.stackcard.data.model.wordList
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.buttons.ExpandIndicator
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.screen.dictionary.TranslationEntry
import com.venom.ui.viewmodel.TranslateViewModel

@Composable
fun BookmarkWordItem(
    word: WordEntity = wordList[0],
    translateViewModel: TranslateViewModel = hiltViewModel(),
    showAll: Boolean,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val state by translateViewModel.uiState.collectAsStateWithLifecycle()
    val rememberedCard = remember(word.englishEn) { word }

    val translations by remember(state.translationResult) {
        derivedStateOf {
            state.translationResult?.dict
        }
    }

    LaunchedEffect(expanded, rememberedCard.englishEn) {
        if (expanded) {
            translateViewModel.onSourceTextChanged(rememberedCard.englishEn)
        }
    }

    ElevatedCard(
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
            )
        ), onClick = { expanded = !expanded }, colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DynamicStyledText(
                    text = word.englishEn,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CustomFilledIconButton(
                        icon = R.drawable.icon_sound,
                        shape = MaterialTheme.shapes.small,
                        isAlpha = true,
                        contentDescription = stringResource(R.string.action_speak),
                        onClick = { onSpeak(word.englishEn) }
                    )
                    CustomFilledIconButton(
                        icon = R.drawable.icon_copy,
                        shape = MaterialTheme.shapes.small,
                        isAlpha = true,
                        contentDescription = stringResource(R.string.action_copy),
                        onClick = { onCopy(word.englishEn) }
                    )
                }
            }

            // Expanded Content
            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = word.englishEn,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    DynamicStyledText(
                        text = word.arabicAr,
                        maxFontSize = 18,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    // Synonyms
                    if (word.synonyms.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Synonyms: ${word.synonyms.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        translations?.forEach { entry ->
                            TranslationEntry(entry = entry,
                                showAll = showAll,
                                onWordClick = {},
                                onSpeak = onSpeak,
                                toggleShowAll = {})
                        }
                    }
                }
            }

            ExpandIndicator(
                expanded = expanded,
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
fun BookmarksScreenPreview() {
    BookmarkWordItem(word = wordList[0],
        onSpeak = {},
        onCopy = {},
        showAll = TODO(),
        modifier = TODO()
    )
}
