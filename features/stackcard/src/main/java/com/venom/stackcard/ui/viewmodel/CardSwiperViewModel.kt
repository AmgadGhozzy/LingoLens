package com.venom.stackcard.ui.viewmodel

import androidx.compose.runtime.Stable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.model.WordLevels
import com.venom.phrase.data.model.Phrase
import com.venom.phrase.data.repo.PhraseRepository
import com.venom.stackcard.data.local.PreferencesKeys
import com.venom.stackcard.data.model.WordEntity
import com.venom.stackcard.data.repo.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

sealed interface CardItem {
    val id: Int
    val englishEn: String
    val arabicAr: String
    val isBookmarked: Boolean
    val isRemembered: Boolean
    val isForgotten: Boolean
}

data class WordCard(val word: WordEntity) : CardItem {
    override val id = word.id
    override val englishEn = word.englishEn
    override val arabicAr = word.arabicAr
    override val isBookmarked = word.isBookmarked
    override val isRemembered = word.isRemembered
    override val isForgotten = word.isForgotten
}

data class PhraseCard(val phrase: Phrase) : CardItem {
    override val id = phrase.phraseId
    override val englishEn = phrase.englishEn
    override val arabicAr = phrase.arabicAr
    override val isBookmarked = phrase.isBookmarked
    override val isRemembered = phrase.isRemembered
    override val isForgotten = phrase.isForgotten
}

@Stable
data class CardSwiperState(
    val visibleCards: List<CardItem> = emptyList(),
    val removedCards: List<CardItem> = emptyList(),
    val isFlipped: Boolean = false,
    val currentCardIndex: Int = 0,
    val error: String? = null,
    val isLoading: Boolean = true,
    val cardType: CardType = CardType.WORD
)

enum class CardType {
    WORD, PHRASE
}

sealed class CardSwiperEvent {
    data class Remove(val card: CardItem) : CardSwiperEvent()
    data class Remember(val card: CardItem) : CardSwiperEvent()
    data class Forgot(val card: CardItem) : CardSwiperEvent()
    data class Bookmark(val card: CardItem) : CardSwiperEvent()
    data class SetCardType(val type: CardType) : CardSwiperEvent()
    object UndoLastAction : CardSwiperEvent()
    object FlipCard : CardSwiperEvent()
    object LoadNextBatch : CardSwiperEvent()
}

@HiltViewModel
class CardSwiperViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val phraseRepository: PhraseRepository,
    private val dataStore: DataStore<Preferences>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialLevel = savedStateHandle.get<String>("level")?.let { levelTitle ->
        WordLevels.values().find { it.id == levelTitle }
    } ?: run {
        runBlocking {
            dataStore.data.firstOrNull()?.get(PreferencesKeys.UNLOCKED_LEVELS)
                ?.let { unlockedLevels ->
                    WordLevels.values()
                        .filter { it.id in unlockedLevels }
                        .maxByOrNull { level ->
                            WordLevels.values().indexOf(level)
                        }
                } ?: WordLevels.Beginner
        }
    }

    private val _selectedLevel = MutableStateFlow(initialLevel)
    val selectedLevel: StateFlow<WordLevels> = _selectedLevel.asStateFlow()

    fun setLevel(level: WordLevels) {
        _selectedLevel.value = level
    }

    private val _state = MutableStateFlow(CardSwiperState())
    val state: StateFlow<CardSwiperState> = _state.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            when (_state.value.cardType) {
                CardType.WORD -> loadWords()
                CardType.PHRASE -> loadPhrases()
            }
        }
    }

    private suspend fun loadWords() {
        val level = selectedLevel
        val words = wordRepository.getWordsFromLevel(level = level.value)

        _state.update { currentState ->
            currentState.copy(
                visibleCards = words.map { word -> WordCard(word) },
                isLoading = false
            )
        }
    }

    private suspend fun loadPhrases() {
        phraseRepository.get10UnseenPhrases().catch { error ->
            _state.update { it.copy(error = error.message, isLoading = false) }
        }.collect { phrases ->
            _state.update {
                it.copy(
                    visibleCards = phrases.map { phrase -> PhraseCard(phrase) }, isLoading = false
                )
            }
        }
    }

    private fun handleCardRemoval(card: CardItem) {
        _state.update { currentState ->
            currentState.copy(
                removedCards = currentState.removedCards + card,
                visibleCards = currentState.visibleCards - card
            )
        }

        if (state.value.visibleCards.isEmpty()) {
            loadCards()
        }
    }

    private fun handleForgot(card: CardItem) {
        viewModelScope.launch {
            when (card) {
                is WordCard -> wordRepository.toggleForgot(card.word)
                is PhraseCard -> phraseRepository.toggleForgot(card.phrase)
            }
        }
    }

    private fun handleRemember(card: CardItem) {
        viewModelScope.launch {
            when (card) {
                is WordCard -> wordRepository.toggleRemember(card.word)
                is PhraseCard -> phraseRepository.toggleRemember(card.phrase)
            }
        }
    }

    private fun handleBookmark(card: CardItem) {
        viewModelScope.launch {
            // Update the database
            when (card) {
                is WordCard -> wordRepository.toggleBookmark(card.word)
                is PhraseCard -> phraseRepository.toggleBookmark(card.phrase)
            }

            // Update the UI state with the new bookmark status
            _state.update { currentState ->
                currentState.copy(
                    visibleCards = currentState.visibleCards.map { existingCard ->
                        if (existingCard.id == card.id) {
                            when (existingCard) {
                                is WordCard -> WordCard(existingCard.word.copy(isBookmarked = !existingCard.isBookmarked))
                                is PhraseCard -> PhraseCard(existingCard.phrase.copy(isBookmarked = !existingCard.isBookmarked))
                            }
                        } else existingCard
                    }
                )
            }
        }
    }

    private fun undoLastAction() {
        viewModelScope.launch {
            _state.value.removedCards.lastOrNull()?.let { lastCard ->
                // Revert card state
                when (lastCard) {
                    is WordCard -> revertWordCardState(lastCard.word)
                    is PhraseCard -> revertPhraseCardState(lastCard.phrase)
                }

                _state.update {
                    it.copy(
                        visibleCards = listOf(lastCard) + it.visibleCards,
                        removedCards = it.removedCards.dropLast(1),
                    )
                }
            }
        }
    }

    private suspend fun revertWordCardState(word: WordEntity) {
        when {
            word.isRemembered -> wordRepository.toggleRemember(word)
            word.isForgotten -> wordRepository.toggleForgot(word)
        }
    }

    private suspend fun revertPhraseCardState(phrase: Phrase) {
        when {
            phrase.isRemembered -> phraseRepository.toggleRemember(phrase)
            phrase.isForgotten -> phraseRepository.toggleForgot(phrase)
        }
    }

    private fun setCardType(type: CardType) {
        _state.update {
            it.copy(
                cardType = type, visibleCards = emptyList(), removedCards = emptyList()
            )
        }
        loadCards()
    }

    fun onEvent(event: CardSwiperEvent) {
        when (event) {
            is CardSwiperEvent.Remove -> handleCardRemoval(event.card)
            is CardSwiperEvent.Forgot -> handleForgot(event.card)
            is CardSwiperEvent.Remember -> handleRemember(event.card)
            is CardSwiperEvent.Bookmark -> handleBookmark(event.card)
            is CardSwiperEvent.SetCardType -> setCardType(event.type)
            is CardSwiperEvent.UndoLastAction -> undoLastAction()
            CardSwiperEvent.FlipCard -> _state.update { it.copy(isFlipped = !it.isFlipped) }
            CardSwiperEvent.LoadNextBatch -> loadCards()
        }
    }
}
