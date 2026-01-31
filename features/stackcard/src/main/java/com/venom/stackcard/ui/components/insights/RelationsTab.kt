package com.venom.stackcard.ui.components.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.data.mock.MockWordData
import com.venom.domain.model.AppTheme
import com.venom.domain.model.WordMaster
import com.venom.resources.R
import com.venom.stackcard.ui.components.mastery.InteractiveText
import com.venom.stackcard.ui.components.mastery.SectionHeader
import com.venom.ui.theme.LingoLensTheme

/**
 * Relations tab content for Insights sheet.
 *
 * Displays:
 * - Synonyms (Teal accent)
 * - Antonyms (Rose accent)
 * - Related concepts (bilingual EN/AR with opacity hierarchy)
 * - Word family (small cards with POS badges)
 *
 * @param word The word to display relations for
 * @param onSpeak Callback for TTS with text and rate
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RelationsTab(
    word: WordMaster,
    onSpeak: (text: String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Cached boolean checks
    val hasSynonyms by remember(word.synonyms) {
        derivedStateOf { word.synonyms.isNotEmpty() }
    }
    val hasAntonyms by remember(word.antonyms) {
        derivedStateOf { word.antonyms.isNotEmpty() }
    }
    val hasRelatedWords by remember(word.relatedWords.english) {
        derivedStateOf { word.relatedWords.english.isNotEmpty() }
    }

    // Pre-compute word family entries
    val wordFamilyEntries = remember(word.wordFamily) {
        listOfNotNull(
            word.wordFamily.noun?.let { "noun" to it },
            word.wordFamily.verb?.let { "verb" to it },
            word.wordFamily.adj?.let { "adj" to it },
            word.wordFamily.adv?.let { "adv" to it }
        )
    }

    val hasWordFamily by remember(wordFamilyEntries) {
        derivedStateOf { wordFamilyEntries.isNotEmpty() }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Synonyms - Teal accent
        if (hasSynonyms) {
            SynonymsSection(
                synonyms = word.synonyms,
                onSpeak = onSpeak
            )
        }

        // Antonyms - Rose accent
        if (hasAntonyms) {
            AntonymsSection(
                antonyms = word.antonyms,
                onSpeak = onSpeak
            )
        }

        // Related concepts - EN prioritized, AR at 60% opacity
        if (hasRelatedWords) {
            RelatedConceptsSection(
                englishWords = word.relatedWords.english,
                arabicWords = word.relatedWords.arabic,
                onSpeak = onSpeak
            )
        }

        // Word family - Small derivative cards
        if (hasWordFamily) {
            WordFamilySection(
                entries = wordFamilyEntries,
                onSpeak = onSpeak
            )
        }
    }
}

/**
 * Synonyms section - Teal accent
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SynonymsSection(
    synonyms: List<String>,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader(
            title = stringResource(R.string.mastery_synonyms),
            icon = painterResource(id = R.drawable.ic_equals)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            synonyms.forEach { synonym ->
                RelationChip(
                    text = synonym,
                    backgroundColor = Color(0xFF14B8A6).copy(alpha = 0.15f), // Teal-500 @ 15%
                    borderColor = Color(0xFF14B8A6).copy(alpha = 0.3f),
                    textColor = Color(0xFF0D9488), // Teal-600
                    onSpeak = onSpeak
                )
            }
        }
    }
}

/**
 * Antonyms section - Rose accent
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AntonymsSection(
    antonyms: List<String>,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader(
            title = stringResource(R.string.mastery_antonyms),
            icon = painterResource(id = R.drawable.ic_arrows_left_right)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            antonyms.forEach { antonym ->
                RelationChip(
                    text = antonym,
                    backgroundColor = Color(0xFFF43F5E).copy(alpha = 0.15f), // Rose-500 @ 15%
                    borderColor = Color(0xFFF43F5E).copy(alpha = 0.3f),
                    textColor = Color(0xFFE11D48), // Rose-600
                    onSpeak = onSpeak
                )
            }
        }
    }
}

/**
 * Related concepts - English prioritized, Arabic at 60% opacity
 */
@Composable
private fun RelatedConceptsSection(
    englishWords: List<String>,
    arabicWords: List<String>,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader(
            title = stringResource(R.string.mastery_related_concepts),
            icon = painterResource(id = R.drawable.ic_circles_three_plus)
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            englishWords.forEachIndexed { index, englishWord ->
                val arabicWord = arabicWords.getOrNull(index)

                RelatedConceptItem(
                    englishWord = englishWord,
                    arabicWord = arabicWord,
                    onSpeak = onSpeak
                )
            }
        }
    }
}

/**
 * Word family - Small derivative cards with POS badges
 */
@Composable
private fun WordFamilySection(
    entries: List<Pair<String, String>>,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader(
            title = stringResource(R.string.mastery_word_family),
            icon = painterResource(id = R.drawable.ic_tree_structure)
        )

        // 2-column grid
        val columns = remember(entries) { entries.chunked(2) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            columns.forEach { columnItems ->
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    columnItems.forEach { (pos, term) ->
                        WordFamilyCard(
                            partOfSpeech = pos,
                            term = term,
                            onSpeak = onSpeak
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable relation chip
 */
@Composable
private fun RelationChip(
    text: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    InteractiveText(
        text = text,
        onSpeak = onSpeak
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = textColor
            )
        }
    }
}

/**
 * Related concept item - Arabic at 60% opacity
 */
@Composable
private fun RelatedConceptItem(
    englishWord: String,
    arabicWord: String?,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    InteractiveText(
        text = englishWord,
        onSpeak = onSpeak
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // English word (primary)
            Text(
                text = englishWord,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (arabicWord != null) {
                // Separator dot
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                )

                // Arabic text with RTL layout - 60% opacity for hierarchy
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = arabicWord,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * Small word family card with POS badge
 */
@Composable
private fun WordFamilyCard(
    partOfSpeech: String,
    term: String,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val posColor = remember(partOfSpeech) {
        when (partOfSpeech.lowercase()) {
            "noun" -> Color(0xFF3B82F6) // Blue
            "verb" -> Color(0xFF10B981) // Green
            "adj" -> Color(0xFFF59E0B) // Amber
            "adv" -> Color(0xFF8B5CF6) // Purple
            else -> Color(0xFF6B7280) // Gray
        }
    }

    InteractiveText(
        text = term,
        onSpeak = onSpeak
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // POS badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(posColor.copy(alpha = 0.15f))
                    .border(
                        0.5.dp,
                        posColor.copy(alpha = 0.3f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = partOfSpeech.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        letterSpacing = 0.8.sp
                    ),
                    color = posColor
                )
            }

            // Word term
            Text(
                text = term,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun RelationsTabPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        RelationsTab(
            word = MockWordData.journeyWord,
            onSpeak = { _ -> }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun RelationsTabPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        RelationsTab(
            word = MockWordData.journeyWord,
            onSpeak = { _ -> }
        )
    }
}