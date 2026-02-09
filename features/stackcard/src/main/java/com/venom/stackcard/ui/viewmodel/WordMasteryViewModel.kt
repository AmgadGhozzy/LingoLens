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
import com.venom.domain.model.UserProgressData
import com.venom.domain.model.UserWordProgress
import com.venom.domain.model.WordMaster
import com.venom.domain.model.getLanguageOptions
import com.venom.domain.repo.IEnrichmentRepository
import com.venom.stackcard.ui.components.insights.InsightsTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

@Immutable
data class CurrentWordProgress(
    val repetitions: Int = 0,
    val nextReviewText: String = "Now",
    val masteryProgress: Float = 0f,
    val isBookmarked: Boolean = false,
    val knownState: KnownState = KnownState.SEEN
)

@Immutable
data class SessionStats(
    val totalCards: Int = 0,
    val masteredCount: Int = 0,
    val learningCount: Int = 0,
    val needsReviewCount: Int = 0,
    val currentStreak: Int = 0,
    val todayWordsViewed: Int = 0,
    val todayXpEarned: Int = 0,
    val totalXp: Int = 0,
    val levelProgress: Float = 0f,
    val xpToNextLevel: Int = 0,
    val totalSessionCount: Int = 0,
    val totalTimeMs: Long = 0,
    val totalDaysActive: Int = 0,
    val bestStreak: Int = 0,
    val totalWordsLearned: Int = 0,
    val totalWordsMastered: Int = 0,
    val sessionDurationMs: Long = 0
)

@Immutable
data class WordMasteryUiState(
    val visibleCards: List<WordMaster> = emptyList(),
    val removedCards: List<WordMaster> = emptyList(),
    val processedCardsCount: Int = 0,
    val currentWord: WordMaster? = null,
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
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
    val isSignedIn: Boolean = true,
    val userName: String? = null,
    val userProgress: UserProgressData? = null,
    val currentWordProgress: CurrentWordProgress = CurrentWordProgress(),
    val sessionStats: SessionStats = SessionStats(),
    val initialCardCount: Int = 0,
    val nextWordProgressCache: CurrentWordProgress? = null
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

private sealed class DbOperation {
    data class RecordSwipeLeft(val userId: String, val wordId: Int) : DbOperation()
    data class RecordSwipeRight(val userId: String, val wordId: Int) : DbOperation()
    data class RecordView(val userId: String, val wordId: Int) : DbOperation()
    data class ToggleBookmark(val userId: String, val wordId: Int, val bookmarked: Boolean) :
        DbOperation()
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

    private val loadDataJob = AtomicReference<Job?>(null)
    private val postLoginSyncJob = AtomicReference<Job?>(null)

    private val sessionStartTime = AtomicLong(0L)
    private val cardsSwipedInSession = AtomicInteger(0)
    private val sessionMasteredCount = AtomicInteger(0)
    private val sessionLearningCount = AtomicInteger(0)
    private val sessionNeedsReviewCount = AtomicInteger(0)

    private val dbOperationChannel = Channel<DbOperation>(Channel.BUFFERED)
    private val wordProgressCache = ConcurrentHashMap<Int, CurrentWordProgress>()
    private val dbSupervisorJob = SupervisorJob()

    init {
        initializeViewModel()
    }

    private fun initializeViewModel() {
        loadUserProgress()

        viewModelScope.launch {
            var previouslySignedIn = identityRepository.isSignedIn()

            identityRepository.authState.collect { authState ->
                val wasSignedIn = previouslySignedIn
                previouslySignedIn = authState.isSignedIn

                _uiState.update {
                    it.copy(
                        isSignedIn = authState.isSignedIn,
                        userName = authState.userName?.takeIf { name -> name.isNotBlank() }
                    )
                }

                if (!wasSignedIn && authState.isSignedIn) {
                    onPostLoginSync()
                }
            }
        }

        startDbOperationProcessor()
    }

    private fun onPostLoginSync() {
        postLoginSyncJob.getAndSet(
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val userId = identityRepository.getCurrentUserId()
                    crashlyticsManager.logBreadcrumb("Post-login sync started for: $userId")

                    progressRepository.syncFromCloud(userId)
                    activityRepository.syncFromCloud(userId)
                    activityRepository.syncAllToCloud(userId)

                    reloadUserProgress(userId)
                    wordProgressCache.clear()

                    _uiState.value.currentWord?.let { word ->
                        prefetchWordProgress(word.id)
                    }

                    crashlyticsManager.logBreadcrumb("Post-login sync completed")
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        crashlyticsManager.logNonFatalException(e, "Post-login sync failed")
                    }
                }
            }
        )?.cancel()
    }

    private suspend fun reloadUserProgress(userId: String) {
        try {
            val dashboard = activityRepository.getDashboardData(userId)

            _uiState.update {
                it.copy(
                    userProgress = UserProgressData(
                        totalXp = dashboard.totalXp,
                        todayXp = dashboard.todayXp,
                        level = dashboard.level,
                        levelProgress = dashboard.levelProgress,
                        xpToNextLevel = dashboard.xpToNextLevel,
                        totalWordsLearned = dashboard.totalWordsViewed,
                        masteredCount = dashboard.totalWordsMastered,
                        currentStreak = dashboard.currentStreak,
                        bestStreak = dashboard.bestStreak,
                        dailyGoalTarget = dashboard.dailyGoalTarget,
                        dailyGoalProgress = dashboard.dailyGoalProgress,
                        dailyGoalMet = dashboard.dailyGoalMet,
                        totalSessionCount = dashboard.totalSessionCount,
                        totalTimeMs = dashboard.totalTimeMs,
                        totalDaysActive = dashboard.totalDaysActive
                    )
                )
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                crashlyticsManager.logNonFatalException(e, "Failed to reload user progress")
            }
        }
    }

    private fun startDbOperationProcessor() {
        viewModelScope.launch(dbSupervisorJob + Dispatchers.IO) {
            dbOperationChannel.receiveAsFlow().collect { operation ->
                try {
                    when (operation) {
                        is DbOperation.RecordSwipeLeft -> {
                            progressRepository.recordSwipeLeft(operation.userId, operation.wordId)
                            progressRepository.recordRecallFail(operation.userId, operation.wordId)
                        }

                        is DbOperation.RecordSwipeRight -> {
                            progressRepository.recordSwipeRight(operation.userId, operation.wordId)
                            progressRepository.recordRecallSuccess(
                                operation.userId,
                                operation.wordId
                            )

                            val progress = progressRepository.getOrCreateProgress(
                                operation.userId,
                                operation.wordId
                            )
                            when (progress.knownState) {
                                KnownState.MASTERED, KnownState.KNOWN -> {
                                    sessionMasteredCount.incrementAndGet()
                                }

                                KnownState.LEARNING -> {
                                    sessionLearningCount.incrementAndGet()
                                }

                                else -> {}
                            }
                            updateSessionStatsInUI()
                        }

                        is DbOperation.RecordView -> {
                            progressRepository.recordView(operation.userId, operation.wordId)
                        }

                        is DbOperation.ToggleBookmark -> {
                            progressRepository.toggleBookmark(
                                operation.userId,
                                operation.wordId,
                                operation.bookmarked
                            )
                        }
                    }
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        crashlyticsManager.logNonFatalException(
                            e,
                            "DB operation failed: $operation"
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        logSessionCompletion()

        loadDataJob.get()?.cancel()
        postLoginSyncJob.get()?.cancel()
        dbSupervisorJob.cancel()
        dbOperationChannel.close()
        wordProgressCache.clear()
    }

    fun onEvent(event: WordMasteryEvent) {
        when (event) {
            is WordMasteryEvent.FlipCard -> flipCard()
            is WordMasteryEvent.OpenSheet -> openSheet()
            is WordMasteryEvent.CloseSheet -> closeSheet()
            is WordMasteryEvent.ChangeTab -> changeTab(event.tab)
            is WordMasteryEvent.ToggleBookmark -> toggleBookmark()
            is WordMasteryEvent.PinLanguage -> pinLanguage(event.language)
            is WordMasteryEvent.RevealHint -> revealHint()
            is WordMasteryEvent.TogglePowerTip -> togglePowerTip()
            is WordMasteryEvent.StartPractice -> startPractice()
            is WordMasteryEvent.PracticeHandled -> practiceHandled()
            is WordMasteryEvent.RemoveCard -> handleRemoveCard(event.word)
            is WordMasteryEvent.SwipeForgot -> handleSwipeForgot(event.word)
            is WordMasteryEvent.SwipeRemember -> handleSwipeRemember(event.word)
            is WordMasteryEvent.RegenerateWords -> loadData(isGenerative = true)
            is WordMasteryEvent.BackToWelcome -> resetToWelcome()
            is WordMasteryEvent.Initialize -> loadData(event.isGenerative, event.topic)
        }
    }

    private fun flipCard() {
        _uiState.update { state ->
            state.copy(
                isFlipped = !state.isFlipped,
                flipRotation = state.flipRotation + 180f
            )
        }
    }

    private fun openSheet() {
        _uiState.update { it.copy(isSheetOpen = true, activeTab = InsightsTab.OVERVIEW) }
    }

    private fun closeSheet() {
        _uiState.update { it.copy(isSheetOpen = false) }
    }

    private fun changeTab(tab: InsightsTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    private fun revealHint() {
        _uiState.update { it.copy(isHintRevealed = true) }
    }

    private fun togglePowerTip() {
        _uiState.update { it.copy(showPowerTip = !it.showPowerTip) }
    }

    private fun startPractice() {
        _uiState.update { it.copy(isPracticeMode = true) }
    }

    private fun practiceHandled() {
        _uiState.update { it.copy(isPracticeMode = false) }
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
        val currentState = _uiState.value
        val remainingCards = currentState.visibleCards - word
        val nextWord = remainingCards.firstOrNull()

        val nextWordProgress = nextWord?.let {
            wordProgressCache[it.id]
        } ?: CurrentWordProgress()

        val isSessionFinished = remainingCards.isEmpty()

        val updatedSessionStats = if (isSessionFinished) {
            val startTime = sessionStartTime.get()
            val duration = if (startTime > 0) System.currentTimeMillis() - startTime else 0L
            currentState.sessionStats.copy(
                masteredCount = sessionMasteredCount.get(),
                learningCount = sessionLearningCount.get(),
                needsReviewCount = sessionNeedsReviewCount.get(),
                sessionDurationMs = duration
            )
        } else {
            currentState.sessionStats.copy(
                masteredCount = sessionMasteredCount.get(),
                learningCount = sessionLearningCount.get(),
                needsReviewCount = sessionNeedsReviewCount.get()
            )
        }

        _uiState.update { state ->
            state.copy(
                visibleCards = remainingCards,
                removedCards = state.removedCards + word,
                currentWord = nextWord,
                processedCardsCount = state.processedCardsCount + 1,
                isFlipped = false,
                isHintRevealed = false,
                showPowerTip = false,
                isSheetOpen = false,
                isBookmarked = nextWordProgress.isBookmarked,
                flipRotation = 0f,
                currentWordProgress = nextWordProgress,
                sessionStats = updatedSessionStats
            )
        }

        if (isSessionFinished) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val userId = identityRepository.getCurrentUserId()
                    val startTime = sessionStartTime.get()
                    val duration = if (startTime > 0) System.currentTimeMillis() - startTime else 0L
                    activityRepository.recordSessionComplete(userId, duration)

                    if (sessionNeedsReviewCount.get() == 0 && cardsSwipedInSession.get() > 0) {
                        activityRepository.recordPerfectSession(userId)
                    }

                    reloadUserProgress(userId)

                    val dashboard = activityRepository.getDashboardData(userId)
                    val wordStats = progressRepository.getSessionStats(userId)
                    _uiState.update { state ->
                        state.copy(
                            sessionStats = state.sessionStats.copy(
                                todayXpEarned = dashboard.todayXp,
                                totalXp = dashboard.totalXp,
                                levelProgress = dashboard.levelProgress,
                                xpToNextLevel = dashboard.xpToNextLevel,
                                totalSessionCount = dashboard.totalSessionCount,
                                totalTimeMs = dashboard.totalTimeMs,
                                totalDaysActive = dashboard.totalDaysActive,
                                bestStreak = dashboard.bestStreak,
                                currentStreak = dashboard.currentStreak,
                                totalWordsLearned = wordStats.totalWordsLearned,
                                totalWordsMastered = dashboard.totalWordsMastered
                            )
                        )
                    }
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        crashlyticsManager.logNonFatalException(e, "Failed to finalize session")
                    }
                }
            }
        } else {
            if (nextWord != null) {
                prefetchWordProgress(nextWord.id)
                remainingCards.getOrNull(1)?.let { secondNext ->
                    prefetchWordProgress(secondNext.id)
                }
            }
        }
    }

    private fun handleSwipeForgot(word: WordMaster) {
        cardsSwipedInSession.incrementAndGet()
        sessionNeedsReviewCount.incrementAndGet()
        updateSessionStatsInUI()

        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            val userId = identityRepository.getCurrentUserId()
            dbOperationChannel.send(DbOperation.RecordSwipeLeft(userId, word.id))
        }

        analyticsManager.logFlashcardSwipe(
            wordId = word.wordEn,
            direction = "left",
            screenName = "FlashcardScreen"
        )
        crashlyticsManager.logBreadcrumb("Swiped forgot: ${word.wordEn}")
    }

    private fun handleSwipeRemember(word: WordMaster) {
        cardsSwipedInSession.incrementAndGet()

        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            val userId = identityRepository.getCurrentUserId()
            dbOperationChannel.send(DbOperation.RecordSwipeRight(userId, word.id))
        }

        analyticsManager.logFlashcardSwipe(
            wordId = word.wordEn,
            direction = "right",
            screenName = "FlashcardScreen"
        )
        crashlyticsManager.logBreadcrumb("Swiped remember: ${word.wordEn}")
    }

    private fun prefetchWordProgress(wordId: Int) {
        if (wordProgressCache.containsKey(wordId)) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = identityRepository.getCurrentUserId()
                val progress = progressRepository.getOrCreateProgress(userId, wordId)
                val wordProgress = progress.toCurrentWordProgress()
                wordProgressCache[wordId] = wordProgress

                if (_uiState.value.currentWord?.id == wordId) {
                    _uiState.update { state ->
                        state.copy(
                            isBookmarked = progress.bookmarked,
                            currentWordProgress = wordProgress
                        )
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    crashlyticsManager.logNonFatalException(e, "Failed to prefetch word progress")
                }
            }
        }
    }

    private fun updateSessionStatsInUI() {
        _uiState.update { state ->
            state.copy(
                sessionStats = state.sessionStats.copy(
                    masteredCount = sessionMasteredCount.get(),
                    learningCount = sessionLearningCount.get(),
                    needsReviewCount = sessionNeedsReviewCount.get()
                )
            )
        }
    }

    private fun loadData(isGenerative: Boolean, topic: String? = null) {
        loadDataJob.getAndSet(
            viewModelScope.launch {
                loadDataInternal(isGenerative, topic)
            }
        )?.cancel()
    }

    private suspend fun loadDataInternal(isGenerative: Boolean, topic: String?) {
        sessionStartTime.set(System.currentTimeMillis())
        cardsSwipedInSession.set(0)
        sessionMasteredCount.set(0)
        sessionLearningCount.set(0)
        sessionNeedsReviewCount.set(0)
        wordProgressCache.clear()

        crashlyticsManager.setCurrentScreen("FlashcardScreen")
        crashlyticsManager.setCurrentFeature("WordMastery")

        _uiState.update {
            it.copy(
                isLoading = true,
                isGenerativeMode = isGenerative,
                error = null
            )
        }

        try {
            val userId = identityRepository.getCurrentUserId()
            val sessionStats = loadSessionStats(userId)

            val words = if (isGenerative) {
                loadGenerativeWords(topic)
            } else {
                loadSrsWords(userId)
            }

            if (words.isNotEmpty()) {
                initializeWithWords(words, sessionStats, isGenerative, topic)
            } else {
                loadFallbackData(
                    Exception("No words available"),
                    topic,
                    "empty_result",
                    sessionStats
                )
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                loadFallbackData(e, topic, "exception_fallback", SessionStats())
            }
        }
    }

    private suspend fun loadGenerativeWords(topic: String?): List<WordMaster> {
        val prompt = if (!topic.isNullOrBlank()) {
            "Generate words related to category: $topic"
        } else {
            "Generate interesting words"
        }

        return enrichmentRepository.generateWords(prompt).getOrElse {
            throw it
        }
    }

    private suspend fun loadSrsWords(userId: String): List<WordMaster> {
        val reviewLimit = 5
        val reviewWords = progressRepository.getWordsDueForReview(userId, reviewLimit)
        val newNeeded = reviewLimit - reviewWords.size

        val newWords = if (newNeeded > 0) {
            enrichmentRepository.enrichAndGetWords(newNeeded)
        } else {
            emptyList()
        }

        return reviewWords + newWords
    }

    private suspend fun initializeWithWords(
        words: List<WordMaster>,
        baseSessionStats: SessionStats,
        isGenerative: Boolean,
        topic: String?
    ) {
        val firstWord = words.firstOrNull()

        val wordProgress = firstWord?.let {
            loadWordProgressDirect(it.id)
        } ?: CurrentWordProgress()

        firstWord?.let { wordProgressCache[it.id] = wordProgress }

        val defaultPinnedLanguage = firstWord?.let {
            getLanguageOptions(it).find { lang -> lang.langName == "Spanish" }
        }

        _uiState.update {
            it.copy(
                visibleCards = words,
                currentWord = firstWord,
                pinnedLanguage = defaultPinnedLanguage,
                isLoading = false,
                flipRotation = 0f,
                initialCardCount = words.size,
                currentWordProgress = wordProgress,
                isBookmarked = wordProgress.isBookmarked,
                sessionStats = baseSessionStats.copy(totalCards = words.size)
            )
        }

        words.drop(1).take(2).forEach { word ->
            prefetchWordProgress(word.id)
        }

        firstWord?.let { word ->
            viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
                val userId = identityRepository.getCurrentUserId()
                dbOperationChannel.send(DbOperation.RecordView(userId, word.id))
            }
        }

        val mode = if (isGenerative) "generative" else "srs_mixed"
        logSessionStarted(mode, words.size, topic)
    }

    private suspend fun loadWordProgressDirect(wordId: Int): CurrentWordProgress {
        return withContext(Dispatchers.IO) {
            try {
                val userId = identityRepository.getCurrentUserId()
                val progress = progressRepository.getOrCreateProgress(userId, wordId)
                progress.toCurrentWordProgress()
            } catch (_: Exception) {
                CurrentWordProgress()
            }
        }
    }

    private suspend fun loadSessionStats(userId: String): SessionStats {
        return withContext(Dispatchers.IO) {
            try {
                val dashboard = activityRepository.getDashboardData(userId)
                val wordStats = progressRepository.getSessionStats(userId)
                SessionStats(
                    totalCards = 0,
                    masteredCount = 0,
                    learningCount = 0,
                    needsReviewCount = 0,
                    currentStreak = dashboard.currentStreak,
                    todayWordsViewed = dashboard.dailyGoalProgress,
                    todayXpEarned = dashboard.todayXp,
                    totalXp = dashboard.totalXp,
                    levelProgress = dashboard.levelProgress,
                    xpToNextLevel = dashboard.xpToNextLevel,
                    totalSessionCount = dashboard.totalSessionCount,
                    totalTimeMs = dashboard.totalTimeMs,
                    totalDaysActive = dashboard.totalDaysActive,
                    bestStreak = dashboard.bestStreak,
                    totalWordsLearned = wordStats.totalWordsLearned,
                    totalWordsMastered = dashboard.totalWordsMastered,
                    sessionDurationMs = 0
                )
            } catch (e: Exception) {
                crashlyticsManager.logNonFatalException(e, "Failed to load session stats")
                SessionStats()
            }
        }
    }

    private fun loadUserProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = identityRepository.getCurrentUserId()
                reloadUserProgress(userId)
            } catch (_: Exception) {
            }
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
        }

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

    private fun resetToWelcome() {
        loadDataJob.get()?.cancel()
        postLoginSyncJob.get()?.cancel()

        sessionMasteredCount.set(0)
        sessionLearningCount.set(0)
        sessionNeedsReviewCount.set(0)
        wordProgressCache.clear()

        _uiState.update {
            WordMasteryUiState(
                isSignedIn = it.isSignedIn,
                userName = it.userName,
                userProgress = it.userProgress,
                isGenerativeMode = it.isGenerativeMode
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

        wordProgressCache[currentWord.id]?.let { cached ->
            wordProgressCache[currentWord.id] = cached.copy(isBookmarked = newStatus)
        }

        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            val userId = identityRepository.getCurrentUserId()
            dbOperationChannel.send(DbOperation.ToggleBookmark(userId, currentWord.id, newStatus))
        }
    }

    private fun logSessionStarted(
        mode: String,
        cardCount: Int,
        topic: String?,
        fallbackReason: String? = null
    ) {
        val params = mutableMapOf<String, Any>(
            "mode" to mode,
            "card_count" to cardCount
        )
        topic?.let { params["topic"] = it }
        fallbackReason?.let { params["fallback_reason"] = it }

        analyticsManager.logFeatureOpened(
            featureName = "flashcard_session",
            additionalParams = params
        )
    }

    private fun logSessionCompletion() {
        val startTime = sessionStartTime.get()
        val cardsSwiped = cardsSwipedInSession.get()

        if (startTime > 0 && cardsSwiped > 0) {
            val sessionDuration = System.currentTimeMillis() - startTime

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
                    "cards_swiped" to cardsSwiped,
                    "cards_remaining" to _uiState.value.visibleCards.size,
                    "mastered" to sessionMasteredCount.get(),
                    "learning" to sessionLearningCount.get(),
                    "needs_review" to sessionNeedsReviewCount.get()
                )
            )
        }
    }

    fun refreshProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = identityRepository.getCurrentUserId()
                reloadUserProgress(userId)
            } catch (_: Exception) {
            }
        }
    }

    private fun UserWordProgress.toCurrentWordProgress(): CurrentWordProgress {
        val totalInteractions = recallSuccess + recallFail + productionSuccess
        val successfulInteractions = recallSuccess + productionSuccess

        val masteryProgress = when {
            totalInteractions == 0 -> 0f
            knownState == KnownState.MASTERED -> 1f
            knownState == KnownState.KNOWN -> 0.75f
            knownState == KnownState.LEARNING -> 0.5f
            knownState == KnownState.SEEN -> 0.25f
            else -> (successfulInteractions.toFloat() / maxOf(totalInteractions, 1))
                .coerceIn(0f, 1f)
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
}