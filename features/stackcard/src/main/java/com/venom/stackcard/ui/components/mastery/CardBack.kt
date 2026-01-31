package com.venom.stackcard.ui.components.mastery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.remember
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

    // Stable lambda for speaking Arabic
    val speakArabic = remember(word.arabicAr, onSpeak) {
        { onSpeak(word.arabicAr) }
    }

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

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(top = 20.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Primary arabic word
            DynamicStyledText(
                text = word.arabicAr,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxFontSize = 40,
                minFontSize = 30,
                lineHeight = 45,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Phonetic Arabic
            Text(
                text = word.phoneticAr,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Transliteration with speaker
            TransliterationRow(
                transliteration = word.translit,
                onClick = speakArabic
            )

            // Memory hook (sticky note)
            word.mnemonicAr?.let { mnemonic ->
                Box(modifier = Modifier.padding(top = 4.dp)) {
                    StickyNoteCard(
                        mnemonicText = mnemonic,
                        wordId = word.id,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Definition block
            Box(modifier = Modifier.padding(top = 16.dp)) {
                DefinitionBlock(
                    definitionEn = word.definitionEn,
                    definitionAr = word.definitionAr
                )
            }

            // Pinned language
            pinnedLanguage?.translation?.let { translation ->
                Box(modifier = Modifier.padding(top = 16.dp)) {
                    PinnedLanguageCard(
                        language = pinnedLanguage,
                        translation = translation
                    )
                }
            }
        }
    }
}

/**
 * Extracted transliteration row for better composition
 */
@Composable
private fun TransliterationRow(
    transliteration: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    InteractiveText(
        text = "",
        onSpeak = { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
        ) {
            Text(
                text = transliteration,
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
}

/**
 * Extracted definition block for better composition and reusability
 */
@Composable
private fun DefinitionBlock(
    definitionEn: String,
    definitionAr: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.3f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(0.2f),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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

        // English definition
        DynamicStyledText(
            text = definitionEn,
            maxFontSize = 24,
            minFontSize = 18,
            lineHeight = 20,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Arabic definition (if exists)
        definitionAr?.let { arabicDef ->
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(0.2f)
            )
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                DynamicStyledText(
                    text = arabicDef,
                    maxFontSize = 24,
                    minFontSize = 18,
                    lineHeight = 24,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Pinned language card
 */
@Composable
private fun PinnedLanguageCard(
    language: LanguageOption,
    translation: String,
    modifier: Modifier = Modifier
) {

    InteractiveText(
        text = translation
    ) {
        Row(
            modifier = modifier
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
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = language.langName.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontSize = 9.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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