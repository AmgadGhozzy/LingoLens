package com.venom.stackcard.ui.screen.quiz

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.domain.model.QuizTestState
import com.venom.domain.model.WordLevels
import com.venom.stackcard.ui.components.QuizCompleted
import com.venom.stackcard.ui.screen.quiz.components.TimerChip
import com.venom.stackcard.ui.viewmodel.QuizUiState
import com.venom.ui.components.buttons.BookmarkFilledButton
import com.venom.ui.components.buttons.SpeechFilledButton

@Composable
fun QuizContent(
    state: QuizUiState,
    onOptionSelected: (String) -> Unit,
    onNext: () -> Unit,
    onComplete: (Boolean, WordLevels?) -> Unit,
    onNavigateToLearn: (WordLevels) -> Unit,
    onRetry: (WordLevels) -> Unit,
) {
    when (val testState = state.testState) {
        is QuizTestState.InProgress -> QuizInProgress(
            state = state,
            testState = testState,
            onOptionSelected = onOptionSelected,
            onNext = onNext,
            onComplete = onComplete
        )

        is QuizTestState.Completed -> QuizCompleted(
            testState = testState,
            onComplete = onComplete,
            onRetry = { onRetry(state.currentLevel) },
            onLearnClick = { onNavigateToLearn(state.currentLevel) }
        )

        else -> Unit
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Preparing Quiz",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ErrorContent(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            FilledTonalButton(
                onClick = onRetry,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Try Again", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ProgressIndicator(progress: Float, timeRemaining: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    )
                    .animateContentSize()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TimerChip(timeRemaining = timeRemaining)
        }
    }
}

@Composable
fun ScoreDisplay(score: Int) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StreakDisplay(streak: Int) {
    val backgroundColor =
        if (streak >= 5) Color(0xFFFF5722) else MaterialTheme.colorScheme.primaryContainer
    val contentColor =
        if (streak >= 5) Color.White else MaterialTheme.colorScheme.onPrimaryContainer

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = streak.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StreakNotification(message: String) {
    Surface(
        color = Color(0xFFFF9800),
        contentColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 12.dp,
        tonalElevation = 8.dp
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActionButtons(
    isBookmarked: Boolean,
    isSpeaking: Boolean,
    onBookmarkClick: () -> Unit,
    onSpeakClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpeechFilledButton(
            isSpeaking = isSpeaking,
            onSpeakClick = onSpeakClick,
            modifier = Modifier.padding(end = 8.dp)
        )

        BookmarkFilledButton(
            isBookmarked = isBookmarked,
            onToggleBookmark = onBookmarkClick
        )
    }
}
