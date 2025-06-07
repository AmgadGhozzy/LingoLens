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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

data class QuizUiState(
    val testState: QuizTestState = QuizTestState.Initial,
    val currentLevel: WordLevels = WordLevels.Beginner,
    val currentWord: WordEntity? = null,
    val options: List<String> = emptyList(),
    val selectedOption: String? = null,
    val isAnswered: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val levelProgress: Map<String, Float> = emptyMap(),
    val unlockedLevels: Set<String> = setOf(WordLevels.Beginner.id),
    val showHeartAnimation: Boolean = false,
    val earnedHeart: Boolean = false,
    val streakMilestone: Boolean = false
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
    private var animationJob: Job? = null

    // Quiz configuration constants
    companion object {
        private const val QUESTION_COUNT = 10
        private const val TIMER_DURATION = 30 // seconds
        private const val PASSING_SCORE_THRESHOLD = 0.7f // 70%

        // Scoring constants
        private const val BASE_POINTS = 10
        private const val STREAK_BONUS = 2
        private const val MAX_STREAK_BONUS = 5
        private const val QUICK_ANSWER_BONUS = 5

        // Heart system constants
        private const val INITIAL_HEARTS = 3
        private const val HEARTS_RECOVERY_THRESHOLD = 5
        private const val MAX_HEARTS = 5
    }

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            loadUnlockedLevels()
            loadLevelProgress()
        }
    }

    private fun loadUnlockedLevels() {
        viewModelScope.launch {
            try {
                dataStore.data
                    .map { preferences ->
                        // Always ensure Beginner level is included
                        val storedLevels = preferences[PreferencesKeys.UNLOCKED_LEVELS] ?: emptySet()
                        storedLevels + WordLevels.Beginner.id
                    }
                    .collect { unlockedLevels ->
                        _state.update { it.copy(unlockedLevels = unlockedLevels) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(unlockedLevels = setOf(WordLevels.Beginner.id)) }
            }
        }
    }

    private fun loadLevelProgress() {
        viewModelScope.launch {
            try {
                val progress = wordRepository.getLevelsProgress()
                _state.update { it.copy(levelProgress = progress) }
                checkAndUnlockLevels(progress)
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    fun refreshProgress() {
        loadLevelProgress()
    }

    fun startQuiz(level: WordLevels) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                currentWords = wordRepository.getWordsFromLevel(
                    level = level,
                    pageSize = QUESTION_COUNT
                )

                if (currentWords.isNotEmpty()) {
                    loadQuestion(0)
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        currentLevel = level,
                        testState = QuizTestState.InProgress(
                            currentQuestion = 1,
                            totalQuestions = QUESTION_COUNT,
                            timeRemaining = TIMER_DURATION,
                            hearts = INITIAL_HEARTS,
                            score = 0f,
                            streak = 0
                        ),
                        showHeartAnimation = false,
                        earnedHeart = false,
                        streakMilestone = false
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

            // Reset timer for each new question
            startTimer(TIMER_DURATION)

            _state.update {
                it.copy(
                    currentWord = currentWord,
                    options = options,
                    selectedOption = null,
                    isAnswered = false,
                    showHeartAnimation = false,
                    earnedHeart = false,
                    streakMilestone = false
                )
            }
        }
    }

    fun selectOption(answer: String) {
        val currentState = _state.value
        if (currentState.isAnswered) return

        val testState = currentState.testState as? QuizTestState.InProgress ?: return
        val isCorrect = answer == currentState.currentWord?.arabicAr

        // Calculate streak and score
        val newStreak = if (isCorrect) testState.streak + 1 else 0
        val pointsEarned = calculatePoints(isCorrect, newStreak, testState.timeRemaining)
        val newScore = testState.score + pointsEarned

        // Hearts management
        val reachedStreakMilestone = isCorrect && newStreak > 0 && newStreak % HEARTS_RECOVERY_THRESHOLD == 0
        val heartRecovery = reachedStreakMilestone && testState.hearts < MAX_HEARTS
        val newHearts = when {
            heartRecovery -> testState.hearts + 1
            !isCorrect -> max(testState.hearts - 1, 0)
            else -> testState.hearts
        }

        // Cancel active timer
        timerJob?.cancel()

        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedOption = answer,
                    isAnswered = true,
                    showHeartAnimation = heartRecovery || !isCorrect,
                    earnedHeart = heartRecovery,
                    streakMilestone = reachedStreakMilestone,
                    testState = testState.copy(
                        hearts = newHearts,
                        streak = newStreak,
                        score = newScore
                    )
                )
            }

            // Handle animations and quiz state after answer
            processAnswerResult(newHearts, testState)
        }
    }

    private fun processAnswerResult(newHearts: Int, testState: QuizTestState.InProgress) {
        // Reset heart animation after a delay
        animationJob?.cancel()
        animationJob = viewModelScope.launch {
            delay(1500)
            _state.update { it.copy(showHeartAnimation = false) }

            // Check if quiz should end or continue
            if (newHearts <= 0 || testState.currentQuestion >= testState.totalQuestions) {
                completeQuiz()
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

        // Calculate final score and determine if passed
        val maxPossibleScore = testState.totalQuestions * BASE_POINTS.toFloat()
        val scorePercentage = (testState.score / maxPossibleScore)
        val passed = scorePercentage >= PASSING_SCORE_THRESHOLD

        // Get next level if applicable
        val nextLevel = getNextLevelIfPassed(passed, currentState.currentLevel)

        // Unlock next level if passed
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
        animationJob?.cancel()
    }

    private fun getNextLevelIfPassed(passed: Boolean, currentLevel: WordLevels): WordLevels? {
        if (!passed) return null

        val levels = WordLevels.values()
        val currentIndex = levels.indexOf(currentLevel)
        return if (currentIndex < levels.size - 1) levels[currentIndex + 1] else null
    }

    private fun checkAndUnlockLevels(progress: Map<String, Float>) {
        val levels = WordLevels.values()

        viewModelScope.launch {
            val currentUnlocked = _state.value.unlockedLevels.toMutableSet()
            var changed = false

            // Check each level in sequence
            for (i in 0 until levels.size - 1) {
                val currentLevel = levels[i]
                val nextLevel = levels[i + 1]
                if (currentUnlocked.contains(currentLevel.id) &&
                    (progress[currentLevel.id] ?: 0f) >= 0.95f &&
                    !currentUnlocked.contains(nextLevel.id)
                ) {
                    currentUnlocked.add(nextLevel.id)
                    changed = true
                }
            }

            if (changed) {
                dataStore.edit { preferences ->
                    preferences[PreferencesKeys.UNLOCKED_LEVELS] = currentUnlocked
                }
                _state.update { it.copy(unlockedLevels = currentUnlocked) }
            }
        }
    }

    private fun unlockLevel(level: WordLevels) {
        viewModelScope.launch {
            try {
                val currentUnlocked = dataStore.data
                    .map { preferences ->
                        preferences[PreferencesKeys.UNLOCKED_LEVELS] ?: setOf(WordLevels.Beginner.id)
                    }
                    .firstOrNull() ?: setOf(WordLevels.Beginner.id)

                val updatedUnlocked = currentUnlocked + level.id
                dataStore.edit { preferences ->
                    preferences[PreferencesKeys.UNLOCKED_LEVELS] = updatedUnlocked
                }
                _state.update { it.copy(unlockedLevels = updatedUnlocked) }
            } catch (_: Exception) {
                // Silent failure
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

                val currentState = _state.value
                val testState = currentState.testState as? QuizTestState.InProgress ?: break

                // Only update timer if question hasn't been answered yet
                if (!currentState.isAnswered) {
                    _state.update {
                        it.copy(
                            testState = testState.copy(timeRemaining = timeLeft)
                        )
                    }

                    // Auto-submit when time runs out
                    if (timeLeft <= 0) {
                        handleTimeOut()
                    }
                }
            }
        }
    }

    private fun handleTimeOut() {
        val currentState = _state.value
        if (currentState.isAnswered) return

        val testState = currentState.testState as? QuizTestState.InProgress ?: return
        val newHearts = max(testState.hearts - 1, 0)

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isAnswered = true,
                    showHeartAnimation = true,
                    testState = testState.copy(
                        hearts = newHearts,
                        streak = 0
                    )
                )
            }

            delay(1500)

            if (newHearts <= 0 || testState.currentQuestion >= testState.totalQuestions) {
                completeQuiz()
            } else {
                onNextQuestion()
            }
        }
    }

    private fun calculatePoints(isCorrect: Boolean, streak: Int, timeRemaining: Int): Float {
        if (!isCorrect) return 0f

        // Base points for correct answer
        var points = BASE_POINTS.toFloat()

        // Add streak bonus (capped)
        val streakBonus = min(streak, MAX_STREAK_BONUS) * STREAK_BONUS
        points += streakBonus

        // Quick answer time bonus if over 2/3 of timer remains
        if (timeRemaining >= TIMER_DURATION * 2 / 3) {
            points += QUICK_ANSWER_BONUS
        }

        return points
    }

    private suspend fun generateOptions(currentWord: WordEntity): List<String> {
        // Get 3 random words from the same level as distractors
        val distractors = wordRepository.getWordsFromLevel(
            level = _state.value.currentLevel,
            orderBy = "RANDOM",
            pageSize = 10
        ).filter { it.id != currentWord.id && it.arabicAr != currentWord.arabicAr }
            .map { it.arabicAr }
            .take(3)

        // Combine correct answer with distractors and shuffle
        return (distractors + currentWord.arabicAr).shuffled()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        animationJob?.cancel()
    }
}