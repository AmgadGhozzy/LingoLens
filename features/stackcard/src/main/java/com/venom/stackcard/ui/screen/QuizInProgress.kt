package com.venom.stackcard.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal =16.dp)
            .systemBarsPadding()
    ) {
        QuizHeader(
            currentQuestion = testState.currentQuestion,
            totalQuestions = testState.totalQuestions,
            hearts = testState.hearts,
            timeRemaining = testState.timeRemaining,
            onBackClick = onBackClick,
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuestionCard(
            question = state.currentWord?.englishEn ?: "",
            translation = state.currentWord?.arabicAr ?: "",
            showTranslation = state.isAnswered
        )

        Spacer(modifier = Modifier.height(24.dp))

        OptionsList(
            options = state.options,
            selectedOption = state.selectedOption,
            isAnswered = state.isAnswered,
            correctAnswer = state.currentWord?.arabicAr,
            onOptionSelected = onOptionSelected
        )
        Spacer(modifier = Modifier.weight(1f))
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