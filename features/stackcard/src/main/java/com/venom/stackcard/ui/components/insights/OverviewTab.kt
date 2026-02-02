package com.venom.stackcard.ui.components.insights

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.venom.data.mock.MockWordData
import com.venom.domain.model.AppTheme
import com.venom.domain.model.CefrLevel
import com.venom.domain.model.WordMaster
import com.venom.domain.model.getDifficulty
import com.venom.resources.R
import com.venom.stackcard.ui.components.mastery.FrequencyWidget
import com.venom.stackcard.ui.components.mastery.InteractiveText
import com.venom.stackcard.ui.components.mastery.PowerCell
import com.venom.stackcard.ui.components.mastery.SectionHeader
import com.venom.stackcard.ui.components.mastery.StatCell
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.tokens.CefrColorScheme
import com.venom.ui.theme.tokens.getCefrColorScheme

/**
 * Overview tab content for Insights sheet.
 *
 * Displays:
 * 1. Your Progress with theme-colored mastery ring
 * 2. Frequency widget with inline Oxford Core badge
 * 3. Semantic Cloud tags
 * 4. Stats grid with enhanced icon visibility
 *
 * @param word The word to display overview for
 * @param showPowerTip Whether the power card flip message is shown
 * @param onTogglePowerTip Callback to toggle power tip visibility
 * @param modifier Modifier for styling
 */
@Composable
fun OverviewTab(
    word: WordMaster,
    showPowerTip: Boolean,
    onTogglePowerTip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val cefrColors = getCefrColorScheme(word.cefrLevel)

    // Derived values
    val hasSemanticTags by remember(word.semanticTags) {
        derivedStateOf { word.semanticTags.isNotEmpty() }
    }

    val isOxfordCore by remember(word.fromOxford) {
        derivedStateOf { word.fromOxford == 1 }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.adp, vertical = 24.adp),
        verticalArrangement = Arrangement.spacedBy(20.adp)
    ) {
        // 1. YOUR PROGRESS - With theme-colored ring
        YourProgressCard(
            repetitions = 2, // TODO: Get from user data
            nextReview = stringResource(R.string.mastery_tomorrow),
            masteryProgress = 0.5f, // TODO: Calculate from user data
            cefrLevel = word.cefrLevel
        )

        // 2. Frequency widget
        FrequencyWidget(frequency = word.frequency)

        // 3. Oxford Core badge
        if (isOxfordCore) {
            OxfordCoreBadge()
        }

        // 3. Semantic Cloud - Horizontal scroll
        if (hasSemanticTags) {
            SemanticCloudSection(
                tags = word.semanticTags,
                scrollState = horizontalScrollState
            )
        }

        // 4. Stats Grid - Enhanced icon visibility
        StatsGrid(
            difficulty = word.cefrLevel.getDifficulty(),
            cefrLevel = word.cefrLevel.displayName,
            cefrColors = cefrColors,
            category = word.category,
            register = word.register,
            showPowerTip = showPowerTip,
            onTogglePowerTip = onTogglePowerTip
        )
    }
}

/**
 * Circular progress ring with dynamic theme color
 */
@Composable
private fun YourProgressCard(
    repetitions: Int,
    nextReview: String,
    masteryProgress: Float,
    cefrLevel: CefrLevel,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = masteryProgress,
        animationSpec = tween(durationMillis = 1000)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.adp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                1.adp,
                MaterialTheme.colorScheme.outline.copy(0.15f),
                RoundedCornerShape(20.adp)
            )
            .padding(20.adp),
        verticalArrangement = Arrangement.spacedBy(16.adp)
    ) {
        SectionHeader(
            title = stringResource(R.string.mastery_your_progress),
            icon = painterResource(id = R.drawable.ic_trend_up)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular mastery ring with theme color
            CircularProgressRing(
                progress = animatedProgress,
                cefrLevel = cefrLevel,
                modifier = Modifier.size(80.adp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.adp),
                color = MaterialTheme.colorScheme.outline.copy(0.2f)
            )

            // Stats
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.adp)
            ) {
                // Repetitions
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = repetitions.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.mastery_repetitions).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.asp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                    )
                }

                // Next review
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.adp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.mastery_next_review).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.asp,
                            fontSize = 9.asp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.adp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.15f))
                            .padding(horizontal = 8.adp, vertical = 3.adp)
                    ) {
                        Text(
                            text = nextReview,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.asp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Circular progress ring with theme-aware color
 */
@Composable
private fun CircularProgressRing(
    progress: Float,
    cefrLevel: CefrLevel,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val themeColor = MaterialTheme.colorScheme.primary //getThemeColorForLevel(cefrLevel)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
    )

    val strokeWidthDp = 8.adp
    Box(
        modifier = modifier
            .drawBehind {
                val strokeWidth = strokeWidthDp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2

                // Background track
                drawArc(
                    color = if (isDarkTheme) {
                        Color.White.copy(alpha = 0.1f)
                    } else {
                        Color.Black.copy(alpha = 0.08f)
                    },
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = Offset(
                        (size.width - radius * 2) / 2,
                        (size.height - radius * 2) / 2
                    ),
                    size = Size(radius * 2, radius * 2)
                )

                // Progress arc with theme color
                drawArc(
                    color = themeColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = Offset(
                        (size.width - radius * 2) / 2,
                        (size.height - radius * 2) / 2
                    ),
                    size = Size(radius * 2, radius * 2)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black
                ),
                color = themeColor
            )
            Text(
                text = "MASTERY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.asp,
                    fontSize = 8.asp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
            )
        }
    }
}

/**
 * Oxford Core badge - optimized
 */
@Composable
private fun OxfordCoreBadge(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.adp))
            .background(MaterialTheme.colorScheme.primary.copy(0.1f))
            .border(
                1.adp,
                MaterialTheme.colorScheme.primary.copy(0.2f),
                RoundedCornerShape(16.adp)
            )
            .padding(16.adp),
        horizontalArrangement = Arrangement.spacedBy(12.adp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_seal_check),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 4.adp)
                .size(20.adp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.adp)) {
            Text(
                text = stringResource(R.string.mastery_oxford_core).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.asp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.mastery_oxford_description),
                style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = 18.asp
                ),
                color = MaterialTheme.colorScheme.primary.copy(0.7f)
            )
        }
    }
}

/**
 * Horizontal scrollable semantic cloud
 */
@Composable
private fun SemanticCloudSection(
    tags: List<String>,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.adp)
    ) {
        SectionHeader(
            title = "Semantic Cloud",
            icon = painterResource(id = R.drawable.ic_cloud)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.adp)
        ) {
            tags.forEach { tag ->
                InteractiveText(text = tag) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.adp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .border(
                                1.adp,
                                MaterialTheme.colorScheme.outline.copy(0.2f),
                                RoundedCornerShape(12.adp)
                            )
                            .padding(horizontal = 14.adp, vertical = 10.adp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Stats grid with enhanced icon visibility (0.8f opacity)
 */
@Composable
private fun StatsGrid(
    difficulty: String,
    cefrLevel: String,
    cefrColors: CefrColorScheme,
    category: String?,
    register: String,
    showPowerTip: Boolean,
    onTogglePowerTip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.adp)
    ) {
        // First row: Difficulty + Power
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.adp)
        ) {
            // Difficulty Cell
            StatCell(
                label = stringResource(R.string.mastery_difficulty),
                watermarkIcon = R.drawable.ic_chart_line_up,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.adp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.adp))
                            .background(cefrColors.background)
                            .border(0.5.adp, cefrColors.border, RoundedCornerShape(4.adp))
                            .padding(horizontal = 6.adp, vertical = 2.adp)
                    ) {
                        Text(
                            text = cefrLevel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.asp
                            ),
                            color = cefrColors.content
                        )
                    }
                    Text(
                        text = difficulty,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.asp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            PowerCell(
                showPowerTip = showPowerTip,
                onTogglePowerTip = onTogglePowerTip,
                modifier = Modifier.weight(1f)
            )
        }

        // Second row: Category + Register
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.adp)
        ) {
            // Category Cell
            category?.let {
                StatCell(
                    label = stringResource(R.string.mastery_category),
                    watermarkIcon = R.drawable.ic_briefcase,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.asp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Register Cell
            StatCell(
                label = stringResource(R.string.mastery_register),
                watermarkIcon = R.drawable.ic_scales,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = register,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun OverviewTabPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        OverviewTab(
            word = MockWordData.journeyWord,
            showPowerTip = false,
            onTogglePowerTip = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun OverviewTabPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        OverviewTab(
            word = MockWordData.journeyWord,
            showPowerTip = true,
            onTogglePowerTip = {}
        )
    }
}