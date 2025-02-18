package com.venom.stackcard.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordLevels
import com.venom.stackcard.ui.viewmodel.QuizViewModel
import com.venom.ui.components.other.ErrorContent

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