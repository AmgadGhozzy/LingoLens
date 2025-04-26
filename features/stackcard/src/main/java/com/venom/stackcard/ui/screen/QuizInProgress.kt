package com.venom.stackcard.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.venom.domain.model.QuizTestState
import com.venom.stackcard.ui.components.NextButton
import com.venom.stackcard.ui.components.OptionsList
import com.venom.stackcard.ui.components.QuestionCard
import com.venom.stackcard.ui.components.QuizHeader
import com.venom.stackcard.ui.viewmodel.QuizUiState
import com.venom.utils.SoundManager
import kotlinx.coroutines.delay

@Composable
fun QuizInProgress(
    state: QuizUiState,
    testState: QuizTestState.InProgress,
    onOptionSelected: (String) -> Unit,
    onNext: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    // State for streak milestone notifications
    var showStreakMilestone by remember { mutableStateOf(false) }
    var streakMessage by remember { mutableStateOf("") }

    // Play sounds and show notifications for streaks
    LaunchedEffect(testState.streak) {
        if (testState.streak > 0 && testState.streak % 5 == 0) {
            soundManager.playSound("streak_milestone")
            streakMessage = "🔥 ${testState.streak}x Streak! Keep going!"
            showStreakMilestone = true
            delay(2000)
            showStreakMilestone = false
        }
    }

    // Play sound for earning hearts
    LaunchedEffect(state.earnedHeart) {
        if (state.earnedHeart) {
            soundManager.playSound("earn_heart")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                QuizHeader(
                    currentQuestion = testState.currentQuestion,
                    totalQuestions = testState.totalQuestions,
                    hearts = testState.hearts,
                    timeRemaining = testState.timeRemaining,
                    streak = testState.streak,
                    score = testState.score,
                    showHeartAnimation = state.showHeartAnimation,
                    earnedHeart = state.earnedHeart,
                    onBackClick = onBackClick
                )
            }

            item {
                QuestionCard(
                    question = state.currentWord?.englishEn ?: "",
                    translation = state.currentWord?.arabicAr ?: "",
                    showTranslation = state.isAnswered
                )
            }

            item {
                OptionsList(
                    options = state.options,
                    selectedOption = state.selectedOption,
                    isAnswered = state.isAnswered,
                    correctAnswer = state.currentWord?.arabicAr,
                    onOptionSelected = onOptionSelected,
                    playAnswerSounds = true
                )
            }
        }

        // Next button
        AnimatedVisibility(
            visible = state.isAnswered,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            NextButton(
                enabled = state.isAnswered,
                onClick = onNext
            )
        }

        // Streak milestone notification
        AnimatedVisibility(
            visible = showStreakMilestone,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Snackbar(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = streakMessage)
            }
        }
    }
}