package com.venom.stackcard.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.venom.domain.model.AppTheme
import com.venom.domain.model.WordProgressState
import com.venom.resources.R
import com.venom.stackcard.ui.components.mastery.AnimatedSuccessIcon
import com.venom.stackcard.ui.components.mastery.GlassOutlinedButton
import com.venom.stackcard.ui.components.mastery.GlassSurface
import com.venom.stackcard.ui.components.mastery.OverallStatItem
import com.venom.stackcard.ui.components.mastery.SessionProgressCard
import com.venom.stackcard.ui.viewmodel.SessionStats
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.buttons.GradientActionButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.components.other.ConfettiView
import com.venom.ui.theme.BrandColors
import com.venom.ui.theme.LingoLensTheme
import kotlinx.coroutines.delay

@Composable
fun SessionFinishedView(
    sessionStats: SessionStats,
    onBackToWelcome: () -> Unit,
    onViewProgress: () -> Unit = {},
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCompleted =
        sessionStats.masteredCount + sessionStats.learningCount + sessionStats.needsReviewCount

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); isVisible = true }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ConfettiView()

        CustomFilledIconButton(
            icon = Icons.Rounded.Close,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.adp),
            onClick = onExit,
            contentDescription = stringResource(R.string.action_close),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.6f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            size = 44.adp
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.adp)
                .fillMaxWidth()
                .scale(scale)
                .graphicsLayer { this.alpha = alpha }
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.adp)
        ) {
            Spacer(modifier = Modifier.height(48.adp))

            AnimatedSuccessIcon()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.adp)
            ) {
                Text(
                    text = stringResource(R.string.session_complete),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.asp,
                        letterSpacing = (-0.5).asp
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.session_complete_message),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.asp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            SessionProgressCard(
                state = WordProgressState(
                    completed = totalCompleted,
                    total = sessionStats.totalCards,
                    mastered = sessionStats.masteredCount,
                    learning = sessionStats.learningCount,
                    needsReview = sessionStats.needsReviewCount,
                    currentStreak = sessionStats.currentStreak,
                    xpEarned = sessionStats.todayXpEarned,
                    xpToNextLevel = if (sessionStats.xpToNextLevel > 0) sessionStats.xpToNextLevel else 100,
                    sessionDurationMs = sessionStats.sessionDurationMs,
                    totalSessionCount = sessionStats.totalSessionCount
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OverallStatsRow(sessionStats)

            Spacer(modifier = Modifier.height(4.adp))

            GradientActionButton(
                text = "Continue Learning",
                onClick = onBackToWelcome,
                leadingIcon = R.drawable.icon_play
            )

            GlassOutlinedButton(
                text = "View Progress",
                onClick = onViewProgress,
                leadingIcon = R.drawable.ic_trend_up
            )

            Spacer(modifier = Modifier.height(28.adp))
        }
    }
}

@Composable
private fun OverallStatsRow(stats: SessionStats) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18,
        alpha = 0.5f,
        contentPadding = 16
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OverallStatItem("Total XP", stats.totalXp.toString(), BrandColors.Purple400, R.drawable.ic_lightning)
            OverallStatItem("Words", stats.totalWordsLearned.toString(), MaterialTheme.colorScheme.primary, R.drawable.ic_book_open)
            OverallStatItem("Sessions", stats.totalSessionCount.toString(), BrandColors.Amber500, R.drawable.ic_flame)
            OverallStatItem("Days", stats.totalDaysActive.toString(), BrandColors.Emerald500, R.drawable.ic_seal_check)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun SessionFinishedPreviewDark() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        SessionFinishedView(
            sessionStats = SessionStats(
                totalCards = 10, masteredCount = 6, learningCount = 3, needsReviewCount = 1,
                currentStreak = 7, todayWordsViewed = 15, todayXpEarned = 85, totalXp = 2450,
                levelProgress = 0.68f, xpToNextLevel = 350, totalSessionCount = 23,
                totalTimeMs = 3600000, totalDaysActive = 15, bestStreak = 12,
                totalWordsLearned = 156, totalWordsMastered = 42, sessionDurationMs = 185000
            ),
            onBackToWelcome = {}, onViewProgress = {}, onExit = {}
        )
    }
}