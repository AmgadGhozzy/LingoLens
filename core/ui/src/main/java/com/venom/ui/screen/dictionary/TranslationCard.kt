package com.venom.ui.screen.dictionary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.venom.domain.model.TranslationResult
import com.venom.resources.R
import com.venom.ui.components.common.ExpandableCard
import com.venom.ui.components.common.adp

@Composable
fun TranslationCard(
    translationResponse: TranslationResult,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sentence = translationResponse.sentences.firstOrNull() ?: return
    var showAlternatives by rememberSaveable { mutableStateOf(false) }

    ExpandableCard(
        title = sentence.translated,
        onSpeak = onSpeak,
        onCopy = onCopy,
        modifier = modifier,
        expandedContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.adp)
            ) {
                // Source text with transliteration
                Column(verticalArrangement = Arrangement.spacedBy(4.adp)) {
                    Text(
                        text = sentence.original,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )

                    sentence.transliteration?.let { translit ->
                        Text(
                            text = "/$translit/",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Light
                        )
                    }

                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.adp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                )

                if (translationResponse.alternatives.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.adp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.alternative_translations),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            )

                            TextButton(onClick = { showAlternatives = !showAlternatives }) {
                                Text(
                                    text = if (showAlternatives)
                                        stringResource(R.string.hide)
                                    else
                                        stringResource(R.string.show),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        AnimatedVisibility(visible = showAlternatives) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.adp),
                                modifier = Modifier.padding(start = 8.adp)
                            ) {
                                translationResponse.alternatives.take(5).forEach { alt ->
                                    Text(
                                        text = "• $alt",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}