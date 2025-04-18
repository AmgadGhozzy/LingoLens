package com.venom.stackcard.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.model.QuizTestState
import com.venom.domain.model.WordLevels
import com.venom.stackcard.data.local.PreferencesKeys
import com.venom.stackcard.data.model.WordEntity
import com.venom.stackcard.data.repo.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val testState: QuizTestState = QuizTestState.Initial,
    val currentLevel: WordLevels = WordLevels.Beginner,
    val currentWord: WordEntity? = null,
    val options: List<String> = emptyList(),
    val selectedOption: String? = null,
    val isAnswered: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val unlockedLevels: Set<String> = setOf(WordLevels.Beginner.id)
)

data class QuizOption(
    val label: String,
    val text: String,
    val isSelected: Boolean = false,
    val isCorrect: Boolean = false
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _state = MutableStateFlow(QuizUiState())
    val state: StateFlow<QuizUiState> = _state.asStateFlow()

    private var currentWords: List<WordEntity> = emptyList()
    private var currentIndex = 0
    private var timerJob: Job? = null

    // Define scoring constants
    private val MAX_POINTS_PER_QUESTION = 10
    private val INITIAL_HEARTS = 3
    private val TIMER_DURATION = 150 // seconds
    private val PASSING_SCORE_THRESHOLD = 0.7f // 70%

    init {
        loadUnlockedLevels()
    }

    private fun loadUnlockedLevels() {
        viewModelScope.launch {
            dataStore.data
                .map { preferences ->
                    preferences[PreferencesKeys.UNLOCKED_LEVELS] ?: setOf(WordLevels.Beginner.id)
                }
                .collect { unlockedLevels ->
                    _state.update { it.copy(unlockedLevels = unlockedLevels) }
                }
        }
    }

    fun startQuiz(level: WordLevels) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val questionCount = 10
                currentWords = wordRepository.getWordsFromLevel(
                    level = level,
                    pageSize = questionCount
                )

                if (currentWords.isNotEmpty()) {
                    loadQuestion(0)
                    startTimer(TIMER_DURATION)
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        currentLevel = level,
                        testState = QuizTestState.InProgress(
                            currentQuestion = 1,
                            totalQuestions = questionCount,
                            timeRemaining = TIMER_DURATION,
                            hearts = INITIAL_HEARTS,
                            score = 0f,
                            streak = 0
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun loadQuestion(index: Int) {
        if (index < currentWords.size) {
            val currentWord = currentWords[index]
            val options = generateOptions(currentWord)
            _state.update {
                it.copy(
                    currentWord = currentWord,
                    options = options,
                    selectedOption = null,
                    isAnswered = false
                )
            }
        }
    }

    fun selectOption(answer: String) {
        val currentState = _state.value
        if (currentState.isAnswered) return

        val testState = currentState.testState as? QuizTestState.InProgress ?: return
        val isCorrect = answer == currentState.currentWord?.arabicAr

        // Calculate streak - increment if correct, reset if wrong
        val newStreak = if (isCorrect) testState.streak + 1 else 0

        // Calculate score - using a consistent point system with streak bonuses
        // Base points for correct answer + streak bonus (capped to avoid excessive score)
        val pointsEarned = if (isCorrect) {
            val streakBonus = minOf(newStreak, 5) // Cap streak bonus at 5x
            MAX_POINTS_PER_QUESTION * streakBonus
        } else {
            0
        }
        val newScore = testState.score + pointsEarned

        // Hearts only decrease on wrong answers
        val newHearts = if (isCorrect) testState.hearts else testState.hearts - 1

        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedOption = answer,
                    isAnswered = true,
                    testState = testState.copy(
                        hearts = newHearts,
                        streak = newStreak,
                        score = newScore
                    )
                )
            }

            delay(1000) // Give user time to see the answer result

            // Check if quiz should end (out of hearts) or proceed to next question
            if (newHearts <= 0) {
                completeQuiz()
            } else if (testState.currentQuestion >= testState.totalQuestions) {
                completeQuiz()
            } else {
                onNextQuestion()
            }
        }
    }

    fun onNextQuestion() {
        val currentState = _state.value.testState as? QuizTestState.InProgress ?: return

        if (currentState.currentQuestion < currentState.totalQuestions) {
            currentIndex++
            viewModelScope.launch {
                loadQuestion(currentIndex)
                _state.update {
                    it.copy(
                        testState = currentState.copy(
                            currentQuestion = currentState.currentQuestion + 1
                        )
                    )
                }
            }
        } else {
            completeQuiz()
        }
    }

    private fun completeQuiz() {
        val currentState = _state.value
        val testState = currentState.testState as? QuizTestState.InProgress ?: return

        // Calculate final score as percentage of maximum possible points
        val maxPossibleScore = testState.totalQuestions * MAX_POINTS_PER_QUESTION
        val scorePercentage = (testState.score / maxPossibleScore)
        val passed = scorePercentage >= PASSING_SCORE_THRESHOLD

        val nextLevel = if (passed) {
            val levels = WordLevels.values()
            val currentIndex = levels.indexOf(currentState.currentLevel)
            if (currentIndex < levels.size - 1) levels[currentIndex + 1] else null
        } else null

        if (passed && nextLevel != null) {
            unlockLevel(nextLevel)
        }

        _state.update {
            it.copy(
                testState = QuizTestState.Completed(
                    level = currentState.currentLevel,
                    levelPercentage = scorePercentage,
                    totalQuestions = testState.totalQuestions,
                    passed = passed,
                    nextLevel = nextLevel
                )
            )
        }

        timerJob?.cancel()
    }

    private fun unlockLevel(level: WordLevels) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                val currentUnlocked = preferences[PreferencesKeys.UNLOCKED_LEVELS] ?: setOf()
                preferences[PreferencesKeys.UNLOCKED_LEVELS] = currentUnlocked + level.id
            }
        }
    }

    private fun startTimer(duration: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var timeLeft = duration
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
                val currentState = _state.value.testState as? QuizTestState.InProgress ?: break
                _state.update {
                    it.copy(
                        testState = currentState.copy(
                            timeRemaining = timeLeft
                        )
                    )
                }
            }
            if (timeLeft <= 0) {
                completeQuiz()
            }
        }
    }

    private suspend fun generateOptions(currentWord: WordEntity): List<String> {
        // Get 4 random words from the same level as distractors
        val distractors = wordRepository.getWordsFromLevel(
            orderBy = "RANDOM",
            pageSize = 4
        ).filter { it.id != currentWord.id }
            .map { it.arabicAr }
            .take(4)

        // Combine correct answer with distractors and shuffle
        return (distractors + currentWord.arabicAr).shuffled()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}