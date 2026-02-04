package com.venom.stackcard.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.analytics.AnalyticsEvent
import com.venom.analytics.AnalyticsManager
import com.venom.analytics.AnalyticsParam
import com.venom.analytics.CrashlyticsManager
import com.venom.analytics.ext.logFlashcardSwipe
import com.venom.data.mock.MockWordData
import com.venom.data.repo.UserActivityRepository
import com.venom.data.repo.UserIdentityRepository
import com.venom.data.repo.UserWordProgressRepository
import com.venom.domain.model.KnownState
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.UserWordProgress
import com.venom.domain.model.WordMaster
import com.venom.domain.model.getLanguageOptions
import com.venom.domain.repo.IEnrichmentRepository
import com.venom.stackcard.ui.components.insights.InsightsTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Current word's user progress data for UI display
 */
@Immutable
data class CurrentWordProgress(
    val repetitions: Int = 0,
    val nextReviewText: String = "Now",
    val masteryProgress: Float = 0f,
    val isBookmarked: Boolean = false,
    val knownState: KnownState = KnownState.SEEN
)

/**
 * Session statistics for progress tracking
 */
@Immutable
data class SessionStats(
    val totalCards: Int = 0,
    val masteredCount: Int = 0,
    val learningCount: Int = 0,
    val needsReviewCount: Int = 0,
    val currentStreak: Int = 0,
    val todayWordsViewed: Int = 0,
    val todayXpEarned: Int = 0
)

@Immutable
data class WordMasteryUiState(
    val visibleCards: List<WordMaster> = emptyList(),
    val removedCards: List<WordMaster> = emptyList(),
    val processedCardsCount: Int = 0,
    val currentWord: WordMaster? = null,
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isFlipping: Boolean = false,
    val isHintRevealed: Boolean = false,
    val isBookmarked: Boolean = false,
    val flipRotation: Float = 0f,
    val isPracticeMode: Boolean = false,
    val isSheetOpen: Boolean = false,
    val activeTab: InsightsTab = InsightsTab.OVERVIEW,
    val pinnedLanguage: LanguageOption? = null,
    val showPowerTip: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGenerativeMode: Boolean = false,

    val isSignedIn: Boolean = false,
    val userName: String? = null,
    // Progress tracking
    val currentWordProgress: CurrentWordProgress = CurrentWordProgress(),
    val sessionStats: SessionStats = SessionStats(),
    val initialCardCount: Int = 0
)

sealed class WordMasteryEvent {
    object FlipCard : WordMasteryEvent()
    object OpenSheet : WordMasteryEvent()
    object CloseSheet : WordMasteryEvent()
    data class ChangeTab(val tab: InsightsTab) : WordMasteryEvent()
    object ToggleBookmark : WordMasteryEvent()
    data class PinLanguage(val language: LanguageOption) : WordMasteryEvent()
    object RevealHint : WordMasteryEvent()
    object TogglePowerTip : WordMasteryEvent()
    object StartPractice : WordMasteryEvent()
    object PracticeHandled : WordMasteryEvent()
    data class RemoveCard(val word: WordMaster) : WordMasteryEvent()
    data class SwipeForgot(val word: WordMaster) : WordMasteryEvent()
    data class SwipeRemember(val word: WordMaster) : WordMasteryEvent()
    object RegenerateWords : WordMasteryEvent()
    object BackToWelcome : WordMasteryEvent()
    data class Initialize(val isGenerative: Boolean, val topic: String? = null) : WordMasteryEvent()
}

@HiltViewModel
class WordMasteryViewModel @Inject constructor(
    private val progressRepository: UserWordProgressRepository,
    private val identityRepository: UserIdentityRepository,
    private val activityRepository: UserActivityRepository,
    private val enrichmentRepository: IEnrichmentRepository,
    private val analyticsManager: AnalyticsManager,
    private val crashlyticsManager: CrashlyticsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordMasteryUiState())
    val uiState: StateFlow<WordMasteryUiState> = _uiState.asStateFlow()

    private var loadDataJob: Job? = null
    private var flipJob: Job? = null
    private val activeJobs = mutableListOf<Job>()
    private var sessionStartTime: Long = 0L
    private var cardsSwipedInSession: Int = 0

    // Session tracking counters (in-session only, for current session display)
    private var sessionMasteredCount = 0
    private var sessionLearningCount = 0
    private var sessionNeedsReviewCount = 0

    override fun onCleared() {
        super.onCleared()
        logSessionCompletion()
        cancelAllJobs()
    }

    private fun cancelAllJobs() {
        loadDataJob?.cancel()
        flipJob?.cancel()
        synchronized(activeJobs) {
            activeJobs.forEach { it.cancel() }
            activeJobs.clear()
        }
    }

    init {
        // observe auth state changes
        viewModelScope.launch {
            identityRepository.authState.collect { authState ->
                _uiState.update {
                    it.copy(
                        isSignedIn = authState.isSignedIn,
                        userName = authState.userName
                    )
                }
            }
        }
    }

    fun onEvent(event: WordMasteryEvent) {
        when (event) {
            is WordMasteryEvent.FlipCard -> flipCard()
            is WordMasteryEvent.OpenSheet -> _uiState.update {
                it.copy(
                    isSheetOpen = true,
                    activeTab = InsightsTab.OVERVIEW
                )
            }

            is WordMasteryEvent.CloseSheet -> _uiState.update { it.copy(isSheetOpen = false) }
            is WordMasteryEvent.ChangeTab -> _uiState.update { it.copy(activeTab = event.tab) }
            is WordMasteryEvent.ToggleBookmark -> toggleBookmark()
            is WordMasteryEvent.PinLanguage -> pinLanguage(event.language)
            is WordMasteryEvent.RevealHint -> _uiState.update { it.copy(isHintRevealed = true) }
            is WordMasteryEvent.TogglePowerTip -> _uiState.update { it.copy(showPowerTip = !it.showPowerTip) }
            is WordMasteryEvent.StartPractice -> _uiState.update { it.copy(isPracticeMode = true) }
            is WordMasteryEvent.PracticeHandled -> _uiState.update { it.copy(isPracticeMode = false) }
            is WordMasteryEvent.RemoveCard -> handleRemoveCard(event.word)
            is WordMasteryEvent.SwipeForgot -> handleSwipeForgot(event.word)
            is WordMasteryEvent.SwipeRemember -> handleSwipeRemember(event.word)
            is WordMasteryEvent.RegenerateWords -> loadData(isGenerative = true)
            is WordMasteryEvent.BackToWelcome -> resetToWelcome()
            is WordMasteryEvent.Initialize -> loadData(event.isGenerative, event.topic)
        }
    }

    private fun loadData(isGenerative: Boolean, topic: String? = null) {
        loadDataJob?.cancel()
        sessionStartTime = System.currentTimeMillis()
        cardsSwipedInSession = 0

        // Reset session counters
        sessionMasteredCount = 0
        sessionLearningCount = 0
        sessionNeedsReviewCount = 0

        crashlyticsManager.setCurrentScreen("FlashcardScreen")
        crashlyticsManager.setCurrentFeature("WordMastery")

        loadDataJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isGenerativeMode = isGenerative,
                    error = null
                )
            }

            try {
                val userId = identityRepository.getCurrentUserId()

                // Load session stats from database (includes streak)
                val sessionStats = loadSessionStats(userId)

                if (isGenerative) {
                    val prompt = if (!topic.isNullOrBlank()) {
                        "Generate words related to category: $topic"
                    } else {
                        "Generate interesting words"
                    }

                    enrichmentRepository.generateWords(prompt).fold(
                        onSuccess = { words ->
                            initializeWithWords(words, null, sessionStats)
                            logSessionStarted("generative", words.size, topic)
                        },
                        onFailure = { e ->
                            loadFallbackData(e, topic, "generative_fallback", sessionStats)
                        }
                    )
                } else {
                    val reviewLimit = 5
                    val reviewWords = progressRepository.getWordsDueForReview(userId, reviewLimit)

                    val newNeeded = reviewLimit - reviewWords.size
                    val newWords = if (newNeeded > 0) {
                        enrichmentRepository.enrichAndGetWords(newNeeded)
                    } else {
                        emptyList()
                    }

                    val words = reviewWords + newWords

                    if (words.isNotEmpty()) {
                        val defaultPinnedLanguage = words.firstOrNull()?.let {
                            getLanguageOptions(it).find { lang -> lang.langName == "Spanish" }
                        }
                        initializeWithWords(words, defaultPinnedLanguage, sessionStats)
                        logSessionStarted(
                            "srs_mixed",
                            words.size,
                            null,
                            "reviews: ${reviewWords.size}, new: ${newWords.size}"
                        )
                    } else {
                        val mockWords = MockWordData.mockWordList.shuffled().take(5)
                        val defaultPinnedLanguage = mockWords.firstOrNull()?.let {
                            getLanguageOptions(it).find { lang -> lang.langName == "Spanish" }
                        }
                        initializeWithWords(mockWords, defaultPinnedLanguage, sessionStats)
                        logSessionStarted("local_mock", mockWords.size, null, "no_data_available")
                    }
                }
            } catch (e: Exception) {
                loadFallbackData(e, topic, "exception_fallback", SessionStats())
            }
        }.also { trackJob(it) }
    }

    /**
     * Load session stats from database - includes streak, mastered counts, etc.
     */
    private suspend fun loadSessionStats(userId: String): SessionStats {
        return try {
            val statsData = progressRepository.getSessionStats(userId)
            SessionStats(
                totalCards = statsData.totalWordsLearned,
                masteredCount = statsData.masteredCount,
                learningCount = statsData.learningCount,
                needsReviewCount = statsData.needsReviewCount,
                currentStreak = statsData.currentStreak,
                todayWordsViewed = statsData.todayWordsViewed,
                todayXpEarned = statsData.todayXpEarned
            )
        } catch (e: Exception) {
            crashlyticsManager.logNonFatalException(e, "Failed to load session stats")
            SessionStats()
        }
    }

    private suspend fun initializeWithWords(
        words: List<WordMaster>,
        pinnedLanguage: LanguageOption?,
        baseSessionStats: SessionStats
    ) {
        val firstWord = words.firstOrNull()
        val wordProgress = firstWord?.let { loadWordProgress(it.id) }

        _uiState.update {
            it.copy(
                visibleCards = words,
                currentWord = firstWord,
                pinnedLanguage = pinnedLanguage,
                isLoading = false,
                flipRotation = 0f,
                initialCardCount = words.size,
                currentWordProgress = wordProgress ?: CurrentWordProgress(),
                sessionStats = baseSessionStats.copy(
                    totalCards = words.size
                )
            )
        }

        firstWord?.let { recordWordView(it.id) }
    }

    private suspend fun loadWordProgress(wordId: Int): CurrentWordProgress {
        return try {
            val userId = identityRepository.getCurrentUserId()
            val progress = progressRepository.getOrCreateProgress(userId, wordId)
            progress.toCurrentWordProgress()
        } catch (_: Exception) {
            CurrentWordProgress()
        }
    }

    private fun UserWordProgress.toCurrentWordProgress(): CurrentWordProgress {
        val totalInteractions = recallSuccess + recallFail + productionSuccess
        val successfulInteractions = recallSuccess + productionSuccess

        // Calculate mastery progress (0.0 to 1.0)
        val masteryProgress = when {
            totalInteractions == 0 -> 0f
            knownState == KnownState.MASTERED -> 1f
            knownState == KnownState.KNOWN -> 0.75f
            knownState == KnownState.LEARNING -> 0.5f
            knownState == KnownState.SEEN -> 0.25f
            else -> (successfulInteractions.toFloat() / maxOf(totalInteractions, 1)).coerceIn(
                0f,
                1f
            )
        }

        val nextReviewText = calculateNextReviewText(nextReview)

        return CurrentWordProgress(
            repetitions = viewCount,
            nextReviewText = nextReviewText,
            masteryProgress = masteryProgress,
            isBookmarked = bookmarked,
            knownState = knownState
        )
    }

    private fun calculateNextReviewText(nextReview: Long?): String {
        if (nextReview == null) return "Now"

        val now = System.currentTimeMillis()
        val diff = nextReview - now

        return when {
            diff <= 0 -> "Now"
            diff < TimeUnit.HOURS.toMillis(1) -> "Soon"
            diff < TimeUnit.HOURS.toMillis(24) -> "Today"
            diff < TimeUnit.DAYS.toMillis(2) -> "Tomorrow"
            diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)} days"
            diff < TimeUnit.DAYS.toMillis(30) -> "${TimeUnit.MILLISECONDS.toDays(diff) / 7} weeks"
            else -> "${TimeUnit.MILLISECONDS.toDays(diff) / 30} months"
        }
    }

    private fun loadFallbackData(
        error: Throwable,
        topic: String?,
        mode: String,
        baseSessionStats: SessionStats
    ) {
        val fallbackWords = MockWordData.mockWordList.shuffled().take(5)
        val defaultPinnedLanguage = fallbackWords.firstOrNull()?.let {
            getLanguageOptions(it).find { lang -> lang.langName == "Spanish" }
        } ?: LanguageOption("Spanish", "")

        _uiState.update {
            it.copy(
                visibleCards = fallbackWords,
                currentWord = fallbackWords.firstOrNull(),
                pinnedLanguage = defaultPinnedLanguage,
                isLoading = false,
                flipRotation = 0f,
                error = null,
                initialCardCount = fallbackWords.size,
                sessionStats = baseSessionStats.copy(totalCards = fallbackWords.size)
            )
        }

        analyticsManager.logError(
            errorType = "word_load",
            errorMessage = error.message ?: "Unknown error",
            screenName = "FlashcardScreen"
        )
        crashlyticsManager.logNonFatalException(error, "Word load failed - using fallback data")
        logSessionStarted(mode, fallbackWords.size, topic, error.message)
    }

    private fun logSessionStarted(
        mode: String,
        cardCount: Int,
        topic: String?,
        fallbackReason: String? = null
    ) {
        val params = mutableMapOf<String, Any>("mode" to mode, "card_count" to cardCount)
        topic?.let { params["topic"] = it }
        fallbackReason?.let { params["fallback_reason"] = it }
        analyticsManager.logFeatureOpened(
            featureName = "flashcard_session",
            additionalParams = params
        )
    }

    private fun resetToWelcome() {
        cancelAllJobs()
        sessionMasteredCount = 0
        sessionLearningCount = 0
        sessionNeedsReviewCount = 0
        _uiState.update { WordMasteryUiState(isGenerativeMode = it.isGenerativeMode) }
    }

    private fun flipCard() {
        if (_uiState.value.isFlipping) return
        flipJob?.cancel()
        flipJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isFlipping = true,
                    isFlipped = !it.isFlipped,
                    flipRotation = it.flipRotation + 180f
                )
            }
            delay(400)
            _uiState.update { it.copy(isFlipping = false) }
        }.also { trackJob(it) }
    }

    private fun pinLanguage(language: LanguageOption?) {
        _uiState.update { state ->
            val newPinned = if (state.pinnedLanguage?.langName == language?.langName) {
                null
            } else {
                language ?: state.pinnedLanguage
            }
            state.copy(pinnedLanguage = newPinned)
        }
    }

    private fun handleRemoveCard(word: WordMaster) {
        viewModelScope.launch {
            val nextWord = (_uiState.value.visibleCards - word).firstOrNull()
            val nextWordProgress = nextWord?.let { loadWordProgress(it.id) }

            _uiState.update { state ->
                state.copy(
                    visibleCards = state.visibleCards - word,
                    removedCards = state.removedCards + word,
                    currentWord = nextWord,
                    processedCardsCount = state.processedCardsCount + 1,
                    isFlipped = false,
                    isHintRevealed = false,
                    showPowerTip = false,
                    isSheetOpen = false,
                    isBookmarked = nextWordProgress?.isBookmarked ?: false,
                    flipRotation = 0f,
                    currentWordProgress = nextWordProgress ?: CurrentWordProgress(),
                    sessionStats = state.sessionStats.copy(
                        masteredCount = sessionMasteredCount,
                        learningCount = sessionLearningCount,
                        needsReviewCount = sessionNeedsReviewCount
                    )
                )
            }

            nextWord?.let { recordWordView(it.id) }
        }
    }

    private fun handleSwipeForgot(word: WordMaster) {
        cardsSwipedInSession++
        sessionNeedsReviewCount++

        viewModelScope.launch {
            val userId = identityRepository.getCurrentUserId()
            progressRepository.recordSwipeLeft(userId, word.id)
            progressRepository.recordRecallFail(userId, word.id)
        }

        updateSessionStatsInUI()
        analyticsManager.logFlashcardSwipe(
            wordId = word.wordEn,
            direction = "left",
            screenName = "FlashcardScreen"
        )
        crashlyticsManager.logBreadcrumb("Swiped forgot: ${word.wordEn}")
    }

    private fun handleSwipeRemember(word: WordMaster) {
        cardsSwipedInSession++

        viewModelScope.launch {
            val userId = identityRepository.getCurrentUserId()
            progressRepository.recordSwipeRight(userId, word.id)
            progressRepository.recordRecallSuccess(userId, word.id)

            // Check updated state to categorize
            val updatedProgress = progressRepository.getOrCreateProgress(userId, word.id)
            when (updatedProgress.knownState) {
                KnownState.MASTERED, KnownState.KNOWN -> sessionMasteredCount++
                KnownState.LEARNING -> sessionLearningCount++
                else -> {}
            }

            updateSessionStatsInUI()
        }

        analyticsManager.logFlashcardSwipe(
            wordId = word.wordEn,
            direction = "right",
            screenName = "FlashcardScreen"
        )
        crashlyticsManager.logBreadcrumb("Swiped remember: ${word.wordEn}")
    }

    private fun updateSessionStatsInUI() {
        _uiState.update { state ->
            state.copy(
                sessionStats = state.sessionStats.copy(
                    masteredCount = sessionMasteredCount,
                    learningCount = sessionLearningCount,
                    needsReviewCount = sessionNeedsReviewCount
                )
            )
        }
    }

    private fun trackJob(job: Job) {
        synchronized(activeJobs) { activeJobs.add(job) }
        job.invokeOnCompletion { synchronized(activeJobs) { activeJobs.remove(job) } }
    }

    private fun logSessionCompletion() {
        if (sessionStartTime > 0 && cardsSwipedInSession > 0) {
            val sessionDuration = System.currentTimeMillis() - sessionStartTime

            // Save session time to activity tracking
            viewModelScope.launch {
                try {
                    val userId = identityRepository.getCurrentUserId()
                    activityRepository.addSessionTime(userId, sessionDuration)
                } catch (e: Exception) {
                    crashlyticsManager.logNonFatalException(e, "Failed to save session time")
                }
            }

            analyticsManager.logFeatureCompleted(
                featureName = "flashcard_session",
                result = "completed",
                durationMs = sessionDuration
            )
            analyticsManager.logEvent(
                eventName = AnalyticsEvent.FEATURE_COMPLETED,
                params = mapOf(
                    AnalyticsParam.FEATURE_NAME to "flashcard_session",
                    AnalyticsParam.DURATION_MS to sessionDuration,
                    "cards_swiped" to cardsSwipedInSession,
                    "cards_remaining" to _uiState.value.visibleCards.size,
                    "mastered" to sessionMasteredCount,
                    "learning" to sessionLearningCount,
                    "needs_review" to sessionNeedsReviewCount
                )
            )
        }
    }

    private fun toggleBookmark() {
        val currentWord = _uiState.value.currentWord ?: return
        val newStatus = !_uiState.value.isBookmarked

        _uiState.update {
            it.copy(
                isBookmarked = newStatus,
                currentWordProgress = it.currentWordProgress.copy(isBookmarked = newStatus)
            )
        }

        viewModelScope.launch {
            progressRepository.toggleBookmark(
                identityRepository.getCurrentUserId(),
                currentWord.id,
                newStatus
            )
        }
    }

    private fun recordWordView(wordId: Int) {
        viewModelScope.launch {
            val userId = identityRepository.getCurrentUserId()
            progressRepository.recordView(userId, wordId)

            // Sync bookmark state and progress from DB
            val progress = progressRepository.getOrCreateProgress(userId, wordId)
            val wordProgress = progress.toCurrentWordProgress()

            _uiState.update {
                it.copy(
                    isBookmarked = progress.bookmarked,
                    currentWordProgress = wordProgress
                )
            }
        }
    }
}