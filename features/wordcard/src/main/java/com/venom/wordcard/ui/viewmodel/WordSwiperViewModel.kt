package com.venom.wordcard.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.wordcard.data.model.WordEntity
import com.venom.wordcard.data.repo.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordSwiperState(
    val words: List<WordEntity> = emptyList(),
    val visibleCards: List<WordEntity> = emptyList(),
    val removedCards: List<WordEntity> = emptyList(),
    val isFlipped: Boolean = false,
    val currentCardIndex: Int = 0,
    val error: String? = null,
    val isLoading: Boolean = true
)

sealed class WordSwiperEvent {
    data class Remove(val card: WordEntity) : WordSwiperEvent()
    data class Remember(val card: WordEntity) : WordSwiperEvent()
    data class Forgot(val card: WordEntity) : WordSwiperEvent()
    data class Bookmark(val card: WordEntity) : WordSwiperEvent()
    object UndoLastAction : WordSwiperEvent()
    object FlipCard : WordSwiperEvent()
}

@HiltViewModel
class WordSwiperViewModel @Inject constructor(
    private val repository: WordRepository
) : ViewModel() {
    private val _state = MutableStateFlow(WordSwiperState())
    val state: StateFlow<WordSwiperState> = _state.asStateFlow()

    init {
        loadWords()
    }

    fun loadWords() {
        viewModelScope.launch {
            repository.get10UnseenWords().catch { error ->
                _state.update { it.copy(error = error.message, isLoading = false) }
            }.collect { words ->
                _state.update {
                    it.copy(
                        words = words, visibleCards = words, isLoading = false
                    )
                }
            }
        }
    }

    private fun handleCardRemoval(card: WordEntity) {
        _state.update { currentState ->
            currentState.copy(
                removedCards = currentState.removedCards + card,
                visibleCards = currentState.visibleCards - card
            )
        }
    }

    private fun handleForgot(card: WordEntity) {
        viewModelScope.launch {
            repository.toggleForgot(card)
            handleCardRemoval(card)
        }
    }

    private fun handleRemember(card: WordEntity) {
        viewModelScope.launch {
            repository.toggleRemember(card)
            handleCardRemoval(card)
        }
    }

    private fun handleBookmark(card: WordEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(card)
            handleCardRemoval(card)
        }
    }

    private fun undoLastAction() {
        viewModelScope.launch {
            _state.update { currentState ->
                if (currentState.removedCards.isNotEmpty()) {
                    val cardToRestore = currentState.removedCards.first()
                    currentState.copy(
                        removedCards = currentState.removedCards.drop(1),
                        visibleCards = listOf(cardToRestore) + currentState.visibleCards
                    )
                } else currentState
            }
        }
    }

    private fun flipCard() {
        _state.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun onEvent(event: WordSwiperEvent) {
        when (event) {
            is WordSwiperEvent.Remove -> handleCardRemoval(event.card)
            is WordSwiperEvent.Forgot -> handleForgot(event.card)
            is WordSwiperEvent.Remember -> handleRemember(event.card)
            is WordSwiperEvent.Bookmark -> handleBookmark(event.card)
            is WordSwiperEvent.UndoLastAction -> undoLastAction()
            WordSwiperEvent.FlipCard -> flipCard()
        }
    }
}
