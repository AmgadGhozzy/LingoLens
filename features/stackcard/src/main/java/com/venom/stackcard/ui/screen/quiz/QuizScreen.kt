package com.venom.stackcard.ui.screen.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.domain.model.WordLevels
import com.venom.stackcard.ui.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    level: WordLevels,
    onNavigateToLearn: (WordLevels) -> Unit,
    onComplete: (Boolean, WordLevels?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(level) {
        viewModel.startQuiz(level)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    center = Offset(0.5f, 0.2f),
                    radius = 1200f
                )
            )
    ) {
        when {
            state.isLoading -> LoadingContent()
            state.error != null -> ErrorContent(onRetry = { viewModel.startQuiz(level) })
            else -> QuizContent(
                state = state,
                onOptionSelected = viewModel::selectOption,
                onNext = viewModel::onNextQuestion,
                onComplete = onComplete,
                onNavigateToLearn = onNavigateToLearn,
                onRetry = { viewModel.startQuiz(level) }
            )
        }
    }
}