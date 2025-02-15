package com.venom.stackcard.domain.model

sealed class QuizTestState {
    object NotStarted : QuizTestState()
    data class InProgress(
        val currentQuestion: Int,
        val totalQuestions: Int,
        val timeRemaining: Int,
        val hearts: Int,
        val score: Int,
        val streak: Int
    ) : QuizTestState()

    data class Completed(
        val score: Float,
        val passed: Boolean,
        val nextLevel: WordLevels?
    ) : QuizTestState()
}
