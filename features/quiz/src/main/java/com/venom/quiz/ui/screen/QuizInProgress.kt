package com.venom.quiz.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.venom.domain.model.WordLevels
import com.venom.quiz.ui.components.NextButton
import com.venom.quiz.ui.components.OptionsList
import com.venom.quiz.ui.components.QuestionCard
import com.venom.quiz.ui.components.QuizHeader
import com.venom.quiz.ui.components.QuizHeaderData
import com.venom.quiz.ui.components.StreakNotification
import com.venom.quiz.ui.viewmodel.QuizUiState
import com.venom.utils.SoundManager
import kotlinx.coroutines.delay

@Composable
fun QuizInProgress(
    state: QuizUiState,
    testState: QuizTestState.InProgress,
    onOptionSelected: (String) -> Unit,
    onNext: () -> Unit,
    onComplete: (Boolean, WordLevels?) -> Unit
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    var showStreakMilestone by remember { mutableStateOf(false) }
    var streakMessage by remember { mutableStateOf("") }

    LaunchedEffect(testState.streak) {
        if (testState.streak > 0 && testState.streak % 5 == 0) {
            soundManager.playSound("streak_milestone")
            streakMessage = "🔥 ${testState.streak}x Streak!"
            showStreakMilestone = true
            delay(2000)
            showStreakMilestone = false
        }
    }

    LaunchedEffect(state.earnedHeart) {
        if (state.earnedHeart) {
            soundManager.playSound("earn_heart")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x1E1E40AF))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                QuizHeader(
                    data = QuizHeaderData(
                        currentQuestion = testState.currentQuestion,
                        totalQuestions = testState.totalQuestions,
                        timeRemaining = testState.timeRemaining,
                        hearts = testState.hearts,
                        streak = testState.streak,
                        score = testState.score,
                        showHeartAnimation = state.showHeartAnimation
                    ),
                    onBackClick = { onComplete(false, state.currentLevel) }
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
                    onOptionSelected = { option -> onOptionSelected(option) }
                )
            }
        }

        AnimatedVisibility(
            visible = state.isAnswered,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            NextButton(onClick = onNext)
        }

        AnimatedVisibility(
            visible = showStreakMilestone,
            enter = slideInVertically { -it } + fadeIn() + scaleIn(),
            exit = slideOutVertically { -it } + fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 120.dp)
        ) {
            StreakNotification(message = streakMessage)
        }
    }
}