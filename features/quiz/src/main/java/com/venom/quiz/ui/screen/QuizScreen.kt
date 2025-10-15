package com.venom.quiz.ui.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.domain.model.QuizTestState
import com.venom.domain.model.WordLevels
import com.venom.quiz.ui.viewmodel.QuizViewModel
import com.venom.utils.SoundManager

@Composable
fun QuizScreen(
    viewModel: QuizViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
    level: WordLevels,
    onNavigateToLearn: (WordLevels) -> Unit,
    onComplete: (Boolean, WordLevels?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(level) {
        viewModel.startQuiz(level)
    }

    // Initialize sound manager
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    // Define local onRetry function
    val onRetry: (WordLevels) -> Unit = { levelToRetry ->
        viewModel.startQuiz(levelToRetry)
    }

    // Use state machine pattern for navigation between different quiz states
    when (val testState = state.testState) {
        is QuizTestState.InProgress -> QuizInProgress(
            state = state,
            testState = testState,
            onOptionSelected = { option ->
                viewModel.selectOption(option)
                // Play sound based on correctness
                if (option == state.currentWord?.arabicAr) {
                    soundManager.playSound("right_answer")
                } else {
                    soundManager.playSound("wrong_answer")
                }
            },
            onNext = {
                viewModel.onNextQuestion()
            },
            onComplete = onComplete
        )

        is QuizTestState.Completed -> QuizCompleted(
            testState = testState,
            onComplete = onComplete,
            onRetry = { onRetry(state.currentLevel) },
            onLearnClick = { onNavigateToLearn(state.currentLevel) }
        )

        else -> Unit
    }
}