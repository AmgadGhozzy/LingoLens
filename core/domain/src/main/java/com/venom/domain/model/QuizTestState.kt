package com.venom.domain.model

sealed class QuizTestState {
    object Initial : QuizTestState()

    data class InProgress(
        val currentQuestion: Int,
        val totalQuestions: Int,
        val timeRemaining: Int,
        val hearts: Int,
        val score: Float,
        val streak: Int
    ) : QuizTestState()

    data class Completed(
        val level: WordLevels,
        val levelPercentage: Float,
        val totalQuestions: Int,
        val passed: Boolean,
        val nextLevel: WordLevels? = null
    ) : QuizTestState()
}
