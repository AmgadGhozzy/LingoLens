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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.venom.ui.theme.BrandColors
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.tokens.CefrColorScheme
import com.venom.ui.theme.tokens.getCefrColorScheme

/**
 * Helper function to get theme color for CEFR level
 * Used for mastery ring and other dynamic theming
 */
@Composable
fun getThemeColorForLevel(cefrLevel: CefrLevel, isDarkTheme: Boolean = true): Color {
    return when (cefrLevel) {
        CefrLevel.A1, CefrLevel.A2 -> if (isDarkTheme) {
            BrandColors.Emerald400
        } else {
            BrandColors.Emerald600
        }

        CefrLevel.B1, CefrLevel.B2 -> if (isDarkTheme) {
            BrandColors.Blue400
        } else {
            BrandColors.Blue600
        }

        CefrLevel.C1, CefrLevel.C2 -> if (isDarkTheme) {
            BrandColors.Purple400
        } else {
            BrandColors.Purple600
        }
    }
}

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
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. YOUR PROGRESS - With theme-colored ring
        YourProgressCard(
            repetitions = 3, // TODO: Get from user data
            nextReview = stringResource(R.string.mastery_tomorrow),
            masteryProgress = 0.65f, // TODO: Calculate from user data
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
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(0.15f),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                modifier = Modifier.size(80.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(0.2f)
            )

            // Stats
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                    )
                }

                // Next review
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.mastery_next_review).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            fontSize = 9.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = nextReview,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
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
    val themeColor = getThemeColorForLevel(cefrLevel, isDarkTheme)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
    )

    Box(
        modifier = modifier
            .drawBehind {
                val strokeWidth = 8.dp.toPx()
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
                    letterSpacing = 0.8.sp,
                    fontSize = 8.sp
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
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary.copy(0.1f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(0.2f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_seal_check),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 4.dp)
                .size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.mastery_oxford_core).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.mastery_oxford_description),
                style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = 18.sp
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader(
            title = "Semantic Cloud",
            icon = painterResource(id = R.drawable.ic_cloud)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                InteractiveText(text = tag) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // First row: Difficulty + Power
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Difficulty Cell
            StatCell(
                label = stringResource(R.string.mastery_difficulty),
                watermarkIcon = R.drawable.ic_chart_line_up,
                modifier = Modifier.weight(1f),
                iconOpacity = 0.08f // Enhanced from 0.05f
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(cefrColors.background)
                            .border(0.5.dp, cefrColors.border, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = cefrLevel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = cefrColors.content
                        )
                    }
                    Text(
                        text = difficulty,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Cell
            category?.let {
                StatCell(
                    label = stringResource(R.string.mastery_category),
                    watermarkIcon = R.drawable.ic_briefcase,
                    modifier = Modifier.weight(1f),
                    iconOpacity = 0.08f // Enhanced from 0.05f
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
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
                modifier = Modifier.weight(1f),
                iconOpacity = 0.08f // Enhanced from 0.05f
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