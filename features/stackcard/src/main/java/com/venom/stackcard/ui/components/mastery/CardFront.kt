package com.venom.stackcard.ui.components.mastery

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.data.mock.MockWordData
import com.venom.domain.model.AppTheme
import com.venom.domain.model.WordMaster
import com.venom.resources.R
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.components.other.GradientGlassCard
import com.venom.ui.theme.BrandColors
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.tokens.getCefrColorScheme
import com.venom.ui.theme.tokens.getDifficultyTheme

/**
 * Front face of the Word Mastery card.
 *
 * Displays:
 * - Header: CEFR badge (left), Difficulty-themed Semantic pill (center), Bookmark button (right)
 * - Hero word with syllable breakdown and rank indicator (dynamic color)
 * - Difficulty Meter Bar below the word
 * - Phonetic pronunciation with audio button
 * - Blurred example sentence as hint (auto-reveals on first tap)
 *
 * @param word Word data to display
 * @param isBookmarked Current bookmark state
 * @param isHintRevealed Whether the example hint is revealed
 * @param onSpeak Callback for TTS with text and rate
 * @param onBookmarkToggle Callback when bookmark is toggled
 * @param onRevealHint Callback when hint reveal button is tapped
 * @param modifier Modifier for styling
 */
@Composable
fun CardFront(
    word: WordMaster,
    isBookmarked: Boolean,
    isHintRevealed: Boolean,
    onSpeak: (text: String) -> Unit,
    onBookmarkToggle: () -> Unit,
    onRevealHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cefrColors = getCefrColorScheme(word.cefrLevel)
    val difficultyTheme = getDifficultyTheme(word.difficultyScore)
    val hintExample by remember(word.cefrLevel, word.examples) {
        derivedStateOf {
            word.examples[word.cefrLevel] ?: word.examples.values.firstOrNull()
        }
    }

    // Animated values
    val blurAmount by animateFloatAsState(
        targetValue = if (isHintRevealed) 0f else 8f,
        animationSpec = tween(durationMillis = 400)
    )

    val hintAlpha by animateFloatAsState(
        targetValue = if (isHintRevealed) 1f else 0.2f,
        animationSpec = tween(durationMillis = 400)
    )

    // Stable lambda references
    val speakWord = remember(word.wordEn, onSpeak) {
        { onSpeak(word.wordEn) }
    }

    GradientGlassCard(
        modifier = modifier.fillMaxWidth(),
        thickness = GlassThickness.UltraThick,
        gradientColors = listOf(
            BrandColors.Blue500,
            difficultyTheme.background
        ),
        gradientAlpha = 0.05f,
        showBorder = true,
        showShadow = true,
        borderColor = difficultyTheme.border,
        shape = RoundedCornerShape(32.dp),
        contentPadding = 20.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlashcardHeader(
                word = word,
                cefrColors = cefrColors,
                difficultyTheme = difficultyTheme,
                isBookmarked = isBookmarked,
                onBookmarkToggle = onBookmarkToggle
            )

            // Hero word section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxWidth()
            ) {
                // Word with rank indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    DynamicStyledText(
                        text = word.wordEn.uppercase(),
                        maxFontSize = 48,
                        minFontSize = 34,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    // Oxford badge + Rank
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_seal_check),
                            contentDescription = stringResource(R.string.mastery_oxford_badge),
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "#${word.rank}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = difficultyTheme.text // Matches difficulty bar
                        )
                    }
                }

                // Syllables
                Text(
                    text = word.syllabify?.uppercase() ?: word.phoneticAr,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.alpha(0.6f)
                )

                // Difficulty Meter
                DifficultyMeter(word.difficultyScore)

                Box(modifier = Modifier.padding(top = 8.dp)) {
                    PhoneticButton(
                        phonetic = word.phoneticUs,
                        onClick = speakWord
                    )
                }
            }

            // Blurred hint example
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth()
            ) {
                if (hintExample != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        InteractiveText(
                            text = hintExample.toString(),
                            onClick = onRevealHint,
                            onSpeak = onSpeak,
                            speakOnTap = isHintRevealed,
                            modifier = Modifier
                                .blur(blurAmount.dp)
                                .alpha(hintAlpha)
                        ) {
                            Text(
                                text = "\"$hintExample\"",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 26.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 4
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.weight(0.8f))
                }

                // Tap indicator
                TapIndicator()
            }
        }
    }
}

/**
 * Phonetic button with IPA notation and speaker icon
 */
@Composable
private fun PhoneticButton(
    phonetic: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    InteractiveText(
        text = "",
        onSpeak = { onClick() }
    ) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.3f))
                .border(
                    0.5.dp,
                    MaterialTheme.colorScheme.outline.copy(0.2f),
                    RoundedCornerShape(50)
                )
                .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "/$phonetic/",
                style = MaterialTheme.typography.titleMedium.copy(
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
            )
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(MaterialTheme.colorScheme.onBackground.copy(0.1f))
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_speaker_high),
                    contentDescription = stringResource(R.string.mastery_speak),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Tap to flip indicator
 */
@Composable
private fun TapIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.alpha(0.3f)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_hand_tap),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.mastery_tap_to_flip).uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun CardFrontPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        CardFront(
            word = MockWordData.journeyWord,
            isBookmarked = false,
            isHintRevealed = false,
            onSpeak = { _ -> },
            onBookmarkToggle = {},
            onRevealHint = {},
            modifier = Modifier.size(320.dp, 580.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun CardFrontPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        CardFront(
            word = MockWordData.journeyWord,
            isBookmarked = false,
            isHintRevealed = false,
            onSpeak = { _ -> },
            onBookmarkToggle = {},
            onRevealHint = {},
            modifier = Modifier.size(320.dp, 580.dp)
        )
    }
}