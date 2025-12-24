package com.venom.quiz.ui.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.domain.model.QuizMode
import com.venom.domain.model.QuizQuestionUi
import com.venom.domain.model.QuizTestState
import com.venom.domain.model.QuizUiState
import com.venom.domain.model.WordLevels
import com.venom.quiz.ui.components.NextButton
import com.venom.quiz.ui.components.OptionsList
import com.venom.quiz.ui.components.QuestionCard
import com.venom.quiz.ui.components.QuizHeader
import com.venom.quiz.ui.components.QuizHeaderData
import com.venom.quiz.ui.viewmodel.UnifiedQuizViewModel

@Composable
fun QuizScreen(
    mode: QuizMode,
    onBack: () -> Unit,
    onComplete: (Boolean, WordLevels?) -> Unit,
    onLearnClick: (WordLevels) -> Unit = {},
    viewModel: UnifiedQuizViewModel = hiltViewModel(LocalActivity.current as ComponentActivity)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(mode) {
        viewModel.start(mode)
    }

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        state.showResults -> {
            val levelPercentage = if (state.totalQuestions == 0) 0f else state.correctCount.toFloat() / state.totalQuestions
            val passed = levelPercentage >= 0.7f
            val level = when (mode) {
                is QuizMode.Word -> mode.level
                else -> WordLevels.Beginner
            }
            val completedState = QuizTestState.Completed(
                level = level,
                levelPercentage = levelPercentage,
                totalQuestions = state.totalQuestions,
                passed = passed,
                nextLevel = null
            )
            QuizCompleted(
                testState = completedState,
                onComplete = onComplete,
                onRetry = { viewModel.start(mode) },
                onLearnClick = { onLearnClick(level) }
            )
        }

        else -> {
            val question = state.currentQuestion
            if (question != null) {
                QuizContent(
                    mode = mode,
                    state = state,
                    question = question,
                    onSelect = { viewModel.selectOption(it) },
                    onNext = { viewModel.nextQuestion() },
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
private fun QuizContent(
    mode: QuizMode,
    state: QuizUiState,
    question: QuizQuestionUi,
    onSelect: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x1E1E40AF))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                QuizHeader(
                    data = QuizHeaderData(
                        currentQuestion = state.currentIndex + 1,
                        totalQuestions = state.totalQuestions,
                        timeRemaining = state.timeRemaining,
                        hearts = state.hearts,
                        streak = state.streak,
                        score = state.score,
                        showHeartAnimation = false
                    ),
                    onBackClick = onBack
                )
            }

            item {
                val translation = (question as? QuizQuestionUi.WordQuestion)?.translation ?: ""
                val showTranslation = state.isAnswered && translation.isNotBlank()
                QuestionCard(
                    question = question.prompt,
                    translation = translation,
                    showTranslation = showTranslation,
                    mode = mode
                )
            }

            item {
                OptionsList(
                    options = question.options,
                    selectedOption = state.selectedOption,
                    isAnswered = state.isAnswered,
                    correctAnswer = question.correctAnswer,
                    onOptionSelected = { onSelect(it) }
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
    }
}