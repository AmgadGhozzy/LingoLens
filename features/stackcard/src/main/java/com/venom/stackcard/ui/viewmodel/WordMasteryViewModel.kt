package com.venom.stackcard.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.analytics.AnalyticsEvent
import com.venom.analytics.AnalyticsManager
import com.venom.analytics.AnalyticsParam
import com.venom.analytics.CrashlyticsManager
import com.venom.analytics.ext.logFlashcardSwipe
import com.venom.data.mock.MockWordData
import com.venom.data.repo.UserIdentityRepository
import com.venom.data.repo.UserWordProgressRepository
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.WordMaster
import com.venom.domain.model.getLanguageOptions
import com.venom.domain.repo.IEnrichmentRepository
import com.venom.stackcard.ui.components.insights.InsightsTab
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    val isGenerativeMode: Boolean = false
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
    private val enrichmentRepository: IEnrichmentRepository,
    private val analyticsManager: AnalyticsManager,
    private val crashlyticsManager: CrashlyticsManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordMasteryUiState())
    val uiState: StateFlow<WordMasteryUiState> = _uiState.asStateFlow()

    private var loadDataJob: Job? = null
    private var flipJob: Job? = null
    private val activeJobs = mutableListOf<Job>()
    private var sessionStartTime: Long = 0L
    private var cardsSwipedInSession: Int = 0

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

    fun onEvent(event: WordMasteryEvent) {
        when (event) {
            is WordMasteryEvent.FlipCard -> flipCard()
            is WordMasteryEvent.OpenSheet -> _uiState.update { it.copy(isSheetOpen = true, activeTab = InsightsTab.OVERVIEW) }
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
        crashlyticsManager.setCurrentScreen("FlashcardScreen")
        crashlyticsManager.setCurrentFeature("WordMastery")

        loadDataJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isGenerativeMode = isGenerative, error = null) }

            try {
                if (isGenerative) {
                    val prompt = if (!topic.isNullOrBlank()) "Generate words related to category: $topic" else "Generate interesting words"
                    enrichmentRepository.generateWords(prompt).fold(
                        onSuccess = { words ->
                            _uiState.update {
                                it.copy(visibleCards = words, currentWord = words.firstOrNull(), pinnedLanguage = null, isLoading = false, flipRotation = 0f)
                            }
                            words.firstOrNull()?.let { recordWordView(it.id) }
                            logSessionStarted("generative", words.size, topic)
                        },
                        onFailure = { e -> loadFallbackData(e, topic, "generative_fallback") }
                    )
                } else {
                    // SRS Priority: 1. Due Reviews, 2. New Words
                    val userId = identityRepository.getCurrentUserId()
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
                        _uiState.update {
                            it.copy(
                                visibleCards = words, 
                                currentWord = words.firstOrNull(), 
                                pinnedLanguage = defaultPinnedLanguage, 
                                isLoading = false, 
                                flipRotation = 0f
                            )
                        }
                        words.firstOrNull()?.let { recordWordView(it.id) }
                        logSessionStarted("srs_mixed", words.size, null, "reviews: ${reviewWords.size}, new: ${newWords.size}")
                    } else {
                        // Fallback only if BOTH reviews and enrichment failed/empty
                        val mockWords = MockWordData.mockWordList.shuffled().take(5)
                        val defaultPinnedLanguage = mockWords.firstOrNull()?.let { 
                            getLanguageOptions(it).find { lang -> lang.langName == "Spanish" } 
                        }
                        _uiState.update {
                            it.copy(
                                visibleCards = mockWords, 
                                currentWord = mockWords.firstOrNull(), 
                                pinnedLanguage = defaultPinnedLanguage, 
                                isLoading = false, 
                                flipRotation = 0f
                            )
                        }
                        mockWords.firstOrNull()?.let { recordWordView(it.id) }
                        logSessionStarted("local_mock", mockWords.size, null, "no_data_available")
                    }
                }
            } catch (e: Exception) {
                loadFallbackData(e, topic, "exception_fallback")
            }
        }.also { trackJob(it) }
    }

    private fun loadFallbackData(error: Throwable, topic: String?, mode: String) {
        val fallbackWords = MockWordData.mockWordList.shuffled().take(5)
        val defaultPinnedLanguage = fallbackWords.firstOrNull()?.let {
            getLanguageOptions(it).find { lang -> lang.langName == "Spanish" }
        } ?: LanguageOption("Spanish", "")

        _uiState.update {
            it.copy(visibleCards = fallbackWords, currentWord = fallbackWords.firstOrNull(), pinnedLanguage = defaultPinnedLanguage, isLoading = false, flipRotation = 0f, error = null)
        }

        analyticsManager.logError(errorType = "word_load", errorMessage = error.message ?: "Unknown error", screenName = "FlashcardScreen")
        crashlyticsManager.logNonFatalException(error, "Word load failed - using fallback data")
        logSessionStarted(mode, fallbackWords.size, topic, error.message)
    }

    private fun logSessionStarted(mode: String, cardCount: Int, topic: String?, fallbackReason: String? = null) {
        val params = mutableMapOf<String, Any>("mode" to mode, "card_count" to cardCount)
        topic?.let { params["topic"] = it }
        fallbackReason?.let { params["fallback_reason"] = it }
        analyticsManager.logFeatureOpened(featureName = "flashcard_session", additionalParams = params)
    }

    private fun resetToWelcome() {
        cancelAllJobs()
        _uiState.update { WordMasteryUiState(isGenerativeMode = it.isGenerativeMode) }
    }

    private fun flipCard() {
        if (_uiState.value.isFlipping) return
        flipJob?.cancel()
        flipJob = viewModelScope.launch {
            _uiState.update { it.copy(isFlipping = true, isFlipped = !it.isFlipped, flipRotation = it.flipRotation + 180f) }
            delay(400)
            _uiState.update { it.copy(isFlipping = false) }
        }.also { trackJob(it) }
    }

    private fun pinLanguage(language: LanguageOption?) {
        _uiState.update { state ->
            val newPinned = if (state.pinnedLanguage?.langName == language?.langName) null else language ?: state.pinnedLanguage
            state.copy(pinnedLanguage = newPinned)
        }
    }

    private fun handleRemoveCard(word: WordMaster) {
        _uiState.update { state ->
            val nextWord = (state.visibleCards - word).firstOrNull()
            if (nextWord != null) {
                recordWordView(nextWord.id)
            }
            state.copy(
                visibleCards = state.visibleCards - word,
                removedCards = state.removedCards + word,
                currentWord = nextWord,
                processedCardsCount = state.processedCardsCount + 1,
                isFlipped = false,
                isHintRevealed = false,
                showPowerTip = false,
                isSheetOpen = false,
                isBookmarked = false,
                flipRotation = 0f
            )
        }
    }

    private fun handleSwipeForgot(word: WordMaster) {
        cardsSwipedInSession++
        viewModelScope.launch {
            progressRepository.recordSwipeLeft(identityRepository.getCurrentUserId(), word.id)
            progressRepository.recordRecallFail(identityRepository.getCurrentUserId(), word.id)
        }
        analyticsManager.logFlashcardSwipe(wordId = word.wordEn, direction = "left", screenName = "FlashcardScreen")
        crashlyticsManager.logBreadcrumb("Swiped forgot: ${word.wordEn}")
    }

    private fun handleSwipeRemember(word: WordMaster) {
        cardsSwipedInSession++
        viewModelScope.launch {
            progressRepository.recordSwipeRight(identityRepository.getCurrentUserId(), word.id)
            progressRepository.recordRecallSuccess(identityRepository.getCurrentUserId(), word.id)
        }
        analyticsManager.logFlashcardSwipe(wordId = word.wordEn, direction = "right", screenName = "FlashcardScreen")
        crashlyticsManager.logBreadcrumb("Swiped remember: ${word.wordEn}")
    }

    private fun trackJob(job: Job) {
        synchronized(activeJobs) { activeJobs.add(job) }
        job.invokeOnCompletion { synchronized(activeJobs) { activeJobs.remove(job) } }
    }

    private fun logSessionCompletion() {
        if (sessionStartTime > 0 && cardsSwipedInSession > 0) {
            val sessionDuration = System.currentTimeMillis() - sessionStartTime
            analyticsManager.logFeatureCompleted(featureName = "flashcard_session", result = "completed", durationMs = sessionDuration)
            analyticsManager.logEvent(
                eventName = AnalyticsEvent.FEATURE_COMPLETED,
                params = mapOf(
                    AnalyticsParam.FEATURE_NAME to "flashcard_session",
                    AnalyticsParam.DURATION_MS to sessionDuration,
                    "cards_swiped" to cardsSwipedInSession,
                    "cards_remaining" to _uiState.value.visibleCards.size
                )
            )
        }
    }

    private fun toggleBookmark() {
        val currentWord = _uiState.value.currentWord ?: return
        val newStatus = !_uiState.value.isBookmarked
        
        _uiState.update { it.copy(isBookmarked = newStatus) }
        
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
            
            // Sync bookmark state from DB
            val progress = progressRepository.getOrCreateProgress(userId, wordId)
            _uiState.update { it.copy(isBookmarked = progress.bookmarked) }
        }
    }
}