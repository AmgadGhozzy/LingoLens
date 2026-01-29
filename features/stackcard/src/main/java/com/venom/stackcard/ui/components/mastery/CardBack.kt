package com.venom.stackcard.ui.components.mastery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.data.mock.MockWordData
import com.venom.domain.model.AppTheme
import com.venom.domain.model.LanguageOption
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
 * Back face of the Word Mastery card.
 *
 * Displays Arabic translation and meaning:
 * - Hero Arabic word with phonetic and transliteration
 * - "Memory Hook" Sticky Note (Randomized)
 * - Definition block (English + Arabic)
 * - Pinned language translation (if set)
 *
 * @param word Word data to display
 * @param isBookmarked Current bookmark state
 * @param pinnedLanguage Optional pinned language to display
 * @param onSpeak Callback for TTS with text and rate
 * @param onBookmarkToggle Callback when bookmark is toggled
 * @param modifier Modifier for styling
 */
@Composable
fun CardBack(
    word: WordMaster,
    isBookmarked: Boolean,
    pinnedLanguage: LanguageOption?,
    onSpeak: (text: String) -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cefrColors = getCefrColorScheme(word.cefrLevel)
    val difficultyTheme = getDifficultyTheme(word.difficultyScore)
    val scrollState = rememberScrollState()

    GradientGlassCard(
        modifier = modifier.fillMaxWidth(),
        thickness = GlassThickness.UltraThick,
        gradientColors = listOf(
            BrandColors.Blue500,
            difficultyTheme.background
        ),
        gradientAlpha = 0.1f,
        showBorder = true,
        showShadow = true,
        borderColor = difficultyTheme.border,
        shape = RoundedCornerShape(32.dp),
        contentPadding = 20.dp
    ) {
        FlashcardHeader(
            word = word,
            cefrColors = cefrColors,
            difficultyTheme = difficultyTheme,
            isBookmarked = isBookmarked,
            onBookmarkToggle = onBookmarkToggle
        )
        Spacer(Modifier.height(20.dp))
        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // primary arabic word
            DynamicStyledText(
                text = word.arabicAr,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxFontSize = 40,
                minFontSize = 30,
                lineHeight = 45,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            // Phonetic Arabic
            Text(
                text = word.phoneticAr,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    // Extra line height for diacritics
                    lineHeight = 30.sp,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // Transliteration with speaker
            InteractiveText(
                text = word.arabicAr,
                onSpeak = onSpeak
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = word.translit,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontStyle = FontStyle.Italic,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_speaker_high),
                            contentDescription = stringResource(R.string.mastery_speak),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // memory hook (sticky note)
            word.mnemonicAr?.let {
                Spacer(Modifier.height(12.dp))
                StickyNoteCard(
                    mnemonicText = it,
                    wordId = word.id,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            // definition block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.3f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(0.2f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_book_open),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.mastery_meaning).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(12.dp))
                DynamicStyledText(
                    text = word.definitionEn,
                    maxFontSize = 24,
                    minFontSize = 18,
                    lineHeight = 20,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Arabic definition
                word.definitionAr?.let {
                    HorizontalDivider(
                        Modifier.padding(vertical = 24.dp),
                        color = MaterialTheme.colorScheme.outline.copy(0.2f)
                    )
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        DynamicStyledText(
                            text = it,
                            maxFontSize = 24,
                            minFontSize = 18,
                            lineHeight = 24,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // pinned language
            pinnedLanguage?.translation?.let { translation ->
                Spacer(Modifier.height(24.dp))
                InteractiveText(
                    text = translation,
                    onSpeak = onSpeak
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.3f))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(0.2f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = pinnedLanguage.langName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 9.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = translation,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Icon(
                            painter = painterResource(id = R.drawable.ic_push_pin_fill),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
                        )
                    }
                }
            }

            // Spacer for scrolling padding
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun CardBackNoPinPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        CardBack(
            word = MockWordData.journeyWord,
            isBookmarked = true,
            pinnedLanguage = null,
            onSpeak = { _ -> },
            onBookmarkToggle = {},
            modifier = Modifier.size(320.dp, 600.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun CardBackNoPinPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        CardBack(
            word = MockWordData.journeyWord,
            isBookmarked = true,
            pinnedLanguage = null,
            onSpeak = { _ -> },
            onBookmarkToggle = {},
            modifier = Modifier.size(320.dp, 600.dp)
        )
    }
}
