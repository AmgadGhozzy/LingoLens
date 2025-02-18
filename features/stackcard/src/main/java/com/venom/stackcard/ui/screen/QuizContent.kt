package com.venom.stackcard.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.venom.domain.model.QuizTestState
import com.venom.domain.model.WordLevels
import com.venom.stackcard.ui.components.QuizCompleted
import com.venom.stackcard.ui.viewmodel.QuizUiState

@Composable
fun QuizContent(
    state: QuizUiState,
    onOptionSelected: (String) -> Unit,
    onNext: () -> Unit,
    onComplete: (Boolean, WordLevels?) -> Unit,
    onRetry: (WordLevels) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
    ) {
        when (val testState = state.testState) {
            is QuizTestState.InProgress -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    QuizInProgress(
                        state = state,
                        testState = testState,
                        onOptionSelected = onOptionSelected,
                        onNext = onNext,
                        onBackClick = { onComplete(false, state.currentLevel) }
                    )
                }
            }

            is QuizTestState.Completed -> {
                QuizCompleted(
                    testState = testState,
                    onComplete = onComplete,
                    onRetry = { onRetry(state.currentLevel) }
                )
            }

            else -> Unit
        }
    }
}
