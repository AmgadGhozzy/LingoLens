package com.venom.stackcard.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.venom.domain.model.AppTheme
import com.venom.resources.R
import com.venom.stackcard.ui.components.mastery.SessionProgressCard
import com.venom.stackcard.ui.components.mastery.WordProgressState
import com.venom.stackcard.ui.viewmodel.SessionStats
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.components.other.ConfettiView
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.lingoLens
import kotlinx.coroutines.delay

@Composable
fun SessionFinishedView(
    sessionStats: SessionStats,
    onBackToWelcome: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCompleted = sessionStats.masteredCount + sessionStats.learningCount + sessionStats.needsReviewCount
    val semantic = MaterialTheme.lingoLens.semantic

    // Entrance animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); isVisible = true }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(400),
        label = "scale"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ConfettiView()

        // Close button
        CustomFilledIconButton(
            icon = Icons.Rounded.Close,
            modifier = Modifier.align(Alignment.TopEnd).padding(20.adp),
            onClick = onExit,
            contentDescription = stringResource(R.string.action_close),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.5f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            size = 44.adp
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.adp)
                .fillMaxWidth()
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.adp)
        ) {
            // Success Icon with glow
            Box(contentAlignment = Alignment.Center) {
                // Glow
                Box(
                    modifier = Modifier
                        .size(110.adp)
                        .clip(CircleShape)
                        .background(semantic.success.copy(alpha = 0.15f))
                )
                // Icon
                Box(
                    modifier = Modifier
                        .size(80.adp)
                        .clip(CircleShape)
                        .background(semantic.success),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_circle_check),
                        contentDescription = null,
                        modifier = Modifier.size(40.adp),
                        tint = semantic.onSuccess
                    )
                }
            }

            // Title & Message
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.adp)
            ) {
                Text(
                    text = stringResource(R.string.session_complete),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.asp
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.session_complete_message),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.asp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Session Progress Card
            SessionProgressCard(
                state = WordProgressState(
                    completed = totalCompleted,
                    total = sessionStats.totalCards,
                    mastered = sessionStats.masteredCount,
                    learning = sessionStats.learningCount,
                    needsReview = sessionStats.needsReviewCount,
                    currentStreak = sessionStats.currentStreak,
                    xpEarned = sessionStats.todayXpEarned,
                    xpToNextLevel = 100
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.adp))

            // Continue Button
            Button(
                onClick = onBackToWelcome,
                modifier = Modifier.widthIn(max = 240.adp).height(52.adp),
                shape = RoundedCornerShape(16.adp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.adp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.adp)
                ) {
                    Text(
                        text = "Continue Learning",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Icon(
                        painter = painterResource(R.drawable.icon_play),
                        contentDescription = null,
                        modifier = Modifier.size(18.adp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun SessionFinishedPreviewDark() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        SessionFinishedView(
            sessionStats = SessionStats(10, 6, 3, 1, 7, 15, 85),
            onBackToWelcome = {},
            onExit = {}
        )
    }
}