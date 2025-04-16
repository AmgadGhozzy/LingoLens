package com.venom.stackcard.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.domain.model.QuizTestState
import com.venom.stackcard.ui.components.NextButton
import com.venom.stackcard.ui.components.OptionsList
import com.venom.stackcard.ui.components.QuestionCard
import com.venom.stackcard.ui.components.QuizHeader
import com.venom.stackcard.ui.viewmodel.QuizUiState

@Composable
fun QuizInProgress(
    state: QuizUiState,
    testState: QuizTestState.InProgress,
    onOptionSelected: (String) -> Unit,
    onNext: () -> Unit,
    onBackClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            QuizHeader(
                currentQuestion = testState.currentQuestion,
                totalQuestions = testState.totalQuestions,
                hearts = testState.hearts,
                timeRemaining = testState.timeRemaining,
                onBackClick = onBackClick,
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
                onOptionSelected = onOptionSelected
            )
        }

        item {
            AnimatedVisibility(
                visible = state.isAnswered,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                NextButton(
                    enabled = state.isAnswered,
                    onClick = onNext
                )
            }
        }
    }
}