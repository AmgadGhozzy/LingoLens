package com.venom.quiz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.theme.QuizColors
import com.venom.ui.theme.QuizColors.ProgressBarForeground

data class QuizHeaderData(
    val currentQuestion: Int,
    val totalQuestions: Int,
    val hearts: Int,
    val timeRemaining: Int,
    val streak: Int,
    val score: Float,
    val showHeartAnimation: Boolean
)

@Composable
fun QuizHeader(
    data: QuizHeaderData,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(QuizColors.BackButtonBackground)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.back_button),
                    tint = QuizColors.BackButtonIcon,
                    modifier = Modifier.size(24.dp)
                )
            }

            ScoreDisplay(score = data.score.toInt())
            StreakDisplay(streak = data.streak)
            HeartsDisplay(hearts = data.hearts)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(
                        R.string.question_progress,
                        data.currentQuestion,
                        data.totalQuestions
                    ),
                    color = ProgressBarForeground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                // Timer moved to the right
                TimerChip(data.timeRemaining)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(QuizColors.ProgressBarBackground)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(data.currentQuestion.toFloat() / data.totalQuestions)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    ProgressBarForeground,
                                    QuizColors.ProgressBarForegroundEnd
                                )
                            )
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewQuizHeader() {
    QuizHeader(
        data = QuizHeaderData(
            currentQuestion = 1,
            totalQuestions = 10,
            hearts = 3,
            timeRemaining = 120,
            streak = 10,
            score = 100f,
            showHeartAnimation = true
        ),
        onBackClick = {}
    )
}