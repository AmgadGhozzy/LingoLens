package com.venom.stackcard.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF232E40), Color(0xFF1A2F40), Color(0xFF1B373A))
                )
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
                        onNext = onNext
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
