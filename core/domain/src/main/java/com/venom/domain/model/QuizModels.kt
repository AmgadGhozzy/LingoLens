package com.venom.domain.model

sealed class QuizQuestionUi {
    abstract val prompt: String
    abstract val isHtml: Boolean
    abstract val options: List<String>
    abstract val correctAnswer: String?

    data class WordQuestion(
        val word: Word,
        override val options: List<String>,
        override val correctAnswer: String,
        val translation: String
    ) : QuizQuestionUi() {
        override val prompt: String = word.englishEn
        override val isHtml: Boolean = false
    }

    data class TestQuestion(
        val question: Question,
        override val options: List<String>,
        override val correctAnswer: String?
    ) : QuizQuestionUi() {
        override val prompt: String = question.question
        override val isHtml: Boolean = true
    }
}

sealed class QuizMode {
    data class Word(val level: WordLevels) : QuizMode()
    data class Test(val testId: Int) : QuizMode()
}

data class QuizUiState(
    val mode: QuizMode? = null,
    val questions: List<QuizQuestionUi> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: String? = null,
    val selectedAnswers: Map<Int, String> = emptyMap(),
    val isAnswered: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val timeRemaining: Int = 0,
    val hearts: Int = 0,
    val streak: Int = 0,
    val score: Float = 0f,
    val showResults: Boolean = false,
    val totalQuestions: Int = 0,
    val correctCount: Int = 0
) {
    val currentQuestion: QuizQuestionUi? get() = questions.getOrNull(currentIndex)
    val progress: Float get() = if (totalQuestions == 0) 0f else (currentIndex + 1f) / totalQuestions
}
