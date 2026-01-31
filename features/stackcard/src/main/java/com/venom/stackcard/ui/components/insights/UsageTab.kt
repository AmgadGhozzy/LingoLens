package com.venom.stackcard.ui.components.insights

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.data.mock.MockWordData
import com.venom.domain.model.AppTheme
import com.venom.domain.model.CefrLevel
import com.venom.domain.model.WordMaster
import com.venom.resources.R
import com.venom.stackcard.ui.components.mastery.InteractiveText
import com.venom.stackcard.ui.components.mastery.SectionHeader
import com.venom.ui.theme.BrandColors
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.tokens.getCefrColorScheme

/**
 * Usage tab content for Insights sheet.
 *
 * Displays:
 * - Teacher's Tip card with Amber glassmorphism
 * - Common Collocations as outlined chips (no background)
 * - Contextual Examples with smart word highlighting and 0.3s animations
 *
 * @param word The word to display usage information for
 * @param onSpeak Callback for TTS with text and rate
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UsageTab(
    word: WordMaster,
    onSpeak: (text: String) -> Unit,
    modifier: Modifier = Modifier,
    userCefrLevel: CefrLevel = CefrLevel.B1 // TODO: Get from user profile
) {
    val scrollState = rememberScrollState()

    // Derived boolean checks
    val hasUsageNote by remember(word.usageNote) {
        derivedStateOf { !word.usageNote.isNullOrBlank() }
    }

    val hasCollocations by remember(word.collocations) {
        derivedStateOf { word.collocations.isNotEmpty() }
    }

    val hasExamples by remember(word.examples) {
        derivedStateOf { word.examples.isNotEmpty() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Teacher's Tip - Amber glassmorphism
        if (hasUsageNote) {
            TeachersTipCard(usageNote = word.usageNote!!)
        }

        // Collocations - Outlined chips
        if (hasCollocations) {
            CollocationsSection(
                collocations = word.collocations,
                onSpeak = onSpeak
            )
        }

        // Contextual Examples - Smart highlighting + 0.3s animations
        if (hasExamples) {
            ContextualExamplesSection(
                examples = word.examples,
                targetWord = word.wordEn, // NEW: Pass word for highlighting
                userCefrLevel = userCefrLevel,
                onSpeak = onSpeak
            )
        }
    }
}

/**
 * Teacher's Tip - Amber glassmorphism with watermark
 */
@Composable
private fun TeachersTipCard(
    usageNote: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BrandColors.Amber400.copy(alpha = 0.12f))
            .border(
                1.dp,
                BrandColors.Amber500.copy(alpha = 0.25f),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        // Watermark lightbulb
        Icon(
            painter = painterResource(id = R.drawable.ic_lightbulb),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 16.dp, y = (-16).dp)
                .alpha(0.06f)
                .rotate(12f),
            tint = BrandColors.Amber500
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BrandColors.Amber400.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_student),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = BrandColors.Amber600
                    )
                }
                Text(
                    text = stringResource(R.string.mastery_teachers_tip).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    ),
                    color = BrandColors.Amber600
                )
            }

            // Usage note
            Text(
                text = "\"$usageNote\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(0.9f)
            )
        }
    }
}

/**
 * Collocations section - Outlined chips with no background
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CollocationsSection(
    collocations: List<String>,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader(
            title = stringResource(R.string.mastery_collocations),
            icon = painterResource(id = R.drawable.ic_link)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            collocations.forEach { collocation ->
                InteractiveText(
                    text = collocation,
                    onSpeak = onSpeak
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Transparent) // NO BACKGROUND
                            .border(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(0.4f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = collocation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

/**
 * Contextual examples - Accordion system with smart highlighting
 */
@Composable
private fun ContextualExamplesSection(
    examples: Map<CefrLevel, String>,
    targetWord: String,
    userCefrLevel: CefrLevel,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader(
            title = stringResource(R.string.mastery_examples),
            icon = painterResource(id = R.drawable.ic_quotes)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            examples.entries
                .sortedBy { it.key.ordinal }
                .forEach { (level, example) ->
                    ExampleAccordionItem(
                        level = level,
                        example = example,
                        targetWord = targetWord,
                        isUserLevel = level == userCefrLevel,
                        onSpeak = onSpeak
                    )
                }
        }
    }
}

/**
 * Accordion item with smart word highlighting and 0.3s animations
 */
@Composable
private fun ExampleAccordionItem(
    level: CefrLevel,
    example: String,
    targetWord: String,
    isUserLevel: Boolean,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember(level, isUserLevel) {
        mutableStateOf(isUserLevel) // Auto-expand only user's level
    }

    val cefrColors = getCefrColorScheme(level)

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "chevron_rotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                width = if (isUserLevel) 1.dp else 0.5.dp,
                color = if (isUserLevel) {
                    MaterialTheme.colorScheme.primary.copy(0.3f)
                } else {
                    MaterialTheme.colorScheme.outline.copy(0.2f)
                },
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        // Header (always visible)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { isExpanded = !isExpanded }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CEFR badge - color-coded
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(cefrColors.background)
                        .border(
                            0.5.dp,
                            cefrColors.border,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = level.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = cefrColors.content
                    )
                }

                // User level indicator
                if (isUserLevel) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "YOUR LEVEL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                letterSpacing = 0.8.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Chevron with smooth rotation
            Icon(
                painter = painterResource(id = R.drawable.ic_caret_down),
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(16.dp)
                    .rotate(rotationAngle),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
            )
        }

        // Content with enhanced 0.3s animation
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(300)
            ),
            exit = shrinkVertically(
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(
                animationSpec = tween(300)
            )
        ) {
            InteractiveText(
                text = example,
                onSpeak = onSpeak
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
                ) {
                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(0.1f))
                            .size(height = 1.dp, width = 1.dp)
                    )

                    // Smart highlighted text
                    HighlightedExampleText(
                        example = example,
                        targetWord = targetWord
                    )
                }
            }
        }
    }
}

/**
 * Text with smart word highlighting
 */
@Composable
private fun HighlightedExampleText(
    example: String,
    targetWord: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val annotatedString = remember(example, targetWord) {
        buildAnnotatedString {
            val lowerExample = example.lowercase()
            val lowerTarget = targetWord.lowercase()

            // Find all occurrences of the word (including variations)
            var lastIndex = 0
            var currentIndex = lowerExample.indexOf(lowerTarget, lastIndex)

            while (currentIndex != -1) {
                // Append text before match
                append(example.substring(lastIndex, currentIndex))

                // Append highlighted match
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryColor
                    )
                ) {
                    append(example.substring(currentIndex, currentIndex + lowerTarget.length))
                }

                lastIndex = currentIndex + lowerTarget.length
                currentIndex = lowerExample.indexOf(lowerTarget, lastIndex)
            }

            // Append remaining text
            if (lastIndex < example.length) {
                append(example.substring(lastIndex))
            }
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(
            lineHeight = 22.sp
            ,color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun UsageTabPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        UsageTab(
            word = MockWordData.journeyWord,
            onSpeak = { _ -> }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun UsageTabPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        UsageTab(
            word = MockWordData.journeyWord,
            onSpeak = { _ -> }
        )
    }
}