package com.venom.stackcard.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordLevels
import com.venom.stackcard.ui.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    level: WordLevels,
    onComplete: (Boolean, WordLevels?) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(level) {
        viewModel.startQuiz(level)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF232E40),
                        Color(0xFF1A2F40),
                        Color(0xFF1B373A)
                    )
                )
            )
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            state.error != null -> {
                ErrorContent(
                    error = state.error!!,
                    onRetry = { viewModel.startQuiz(level) }
                )
            }

            else -> {
                QuizContent(
                    state = state,
                    onOptionSelected = viewModel::selectOption,
                    onNext = viewModel::onNextQuestion,
                    onComplete = onComplete,
                    onRetry = { retryLevel ->
                        viewModel.startQuiz(retryLevel)
                    }
                )
            }
        }
    }
}